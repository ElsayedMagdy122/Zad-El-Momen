package dev.sayed.mehrabalmomen.presentation.screen.prayers

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.repository.notification.NotificationScheduler
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerNotificationsRepository
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.BatteryOptimizationRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import dev.sayed.mehrabalmomen.domain.utils.Logger
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.BatteryOptimizationInteractionListener
import dev.sayed.mehrabalmomen.presentation.utils.AnalyticsHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PrayerTimesViewModel(
    private val prayerRepository: PrayerRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationsRepository: PrayerNotificationsRepository,
    private val prayerSchedulingUseCase: PrayerSchedulingUseCase,
    private val batteryOptimizationRepository: BatteryOptimizationRepository,
    private val alarmScheduler: AlarmScheduler,
    private val notificationScheduler: NotificationScheduler,
    private val analyticsHelper: AnalyticsHelper,
    private val logger: Logger,
) : BaseViewModel<PrayerTimesUiState, PrayerTimesEffect>(PrayerTimesUiState()),
    PrayerTimesInteractionListener, BatteryOptimizationInteractionListener {

    private var countdownJob: Job? = null
    private val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    private val _countdownTime = MutableStateFlow(PrayerTimesUiState.TimeUiState())
    val countdownTime: StateFlow<PrayerTimesUiState.TimeUiState> = _countdownTime.asStateFlow()

    init {
        getDailyPrayers()
        getNextPrayer()
        observePrayerNotifications()
        refreshBatteryStatus()
    }

    /**
     * Updates the UI state with the current battery optimization status.
     */
    fun refreshBatteryStatus() {
        val isIgnoring = batteryOptimizationRepository.isIgnoringBatteryOptimizations()
        updateState { it.copy(isBatteryOptimizationEnabled = isIgnoring) }
    }

    private fun scheduleAlarmsIfNeeded() {
        viewModelScope.launch {
            try {
                prayerSchedulingUseCase.rescheduleTodayPrayerAlarms()
            } catch (e: Exception) {
                logger.e("AZAN_DEBUG", "Failed to schedule alarms", e)
            }
        }
    }

    fun onScreenOpened() {
        analyticsHelper.logScreen("PrayerTimes")
    }

    /**
     * Retrieves the list of prayer times for the current day and updates the state.
     */
    private fun getDailyPrayers() {
        viewModelScope.launch {
            try {
                val settings = settingsRepository.observeAppSettings().first().prayerSettings
                val prayers = prayerRepository.getDailyPrayers(
                    madhab = settings.madhab,
                    calculationMethod = settings.calculationMethod,
                    location = settings.location,
                    date = today,
                )
                val nextPrayer = prayerRepository.getNextPrayer(
                    instant = Clock.System.now(),
                    madhab = settings.madhab,
                    calculationMethod = settings.calculationMethod,
                    location = settings.location,
                    date = today,
                )
                val notifications = notificationsRepository.observeAll().first()
                val zone = TimeZone.currentSystemDefault()

                val prayerUiStates = prayers.map { prayer ->
                    prayer.toPrayerUiState(zone).copy(
                        isNotificationEnabled = notifications[prayer.name] ?: true,
                        isUpComing = prayer.name == nextPrayer.name
                    )
                }

                val now = Clock.System.now().toEpochMilliseconds()
                val prayersWithProgress = calculatePrayerProgress(prayerUiStates, now)

                updateState {
                    it.copy(
                        prayers = prayersWithProgress
                    )
                }
            } catch (e: Exception) {
                logger.e("PrayerTimesVM", "Error getting daily prayers", e)
            }
        }
    }

    /**
     * Identifies the next prayer and starts the countdown timer.
     */
    private fun getNextPrayer() {
        viewModelScope.launch {
            try {
                val settings = settingsRepository.observeAppSettings().first().prayerSettings
                val next = prayerRepository.getNextPrayer(
                    instant = Clock.System.now(),
                    madhab = settings.madhab,
                    calculationMethod = settings.calculationMethod,
                    location = settings.location,
                    date = today,
                )
                val zone = TimeZone.currentSystemDefault()
                updateState {
                    it.copy(
                        nextPrayer = next.toPrayerUiState(zone).copy(isUpComing = true)
                    )
                }
                startCountdown(next.time.toEpochMilliseconds())
            } catch (e: Exception) {
                logger.e("PrayerTimesVM", "Error getting next prayer", e)
            }
        }
    }

    /**
     * Starts a periodic timer that updates the remaining time until the next prayer.
     */
    private fun startCountdown(targetMillis: Long) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                val now = Clock.System.now().toEpochMilliseconds()
                val diff: Long = targetMillis - now
                if (diff <= 0L) {
                    handleCountdownFinished()
                    break
                }
                val hours = (diff / (1000 * 60 * 60)) % 24
                val minutes = (diff / (1000 * 60)) % 60
                val seconds = (diff / 1000) % 60

                _countdownTime.value = PrayerTimesUiState.TimeUiState(
                    hours = hours.toString().padStart(2, '0'),
                    minutes = minutes.toString().padStart(2, '0'),
                    seconds = seconds.toString().padStart(2, '0')
                )
                delay(1.seconds)
            }
        }
    }

    private fun handleCountdownFinished() {
        getNextPrayer()
        getDailyPrayers()
        scheduleAlarmsIfNeeded()
    }

    private fun observePrayerNotifications() {
        notificationsRepository.observeAll()
            .onEach {
                getDailyPrayers()
            }
            .launchIn(viewModelScope)
    }

    private fun calculatePrayerProgress(
        prayers: List<PrayerTimesUiState.PrayerUiState>,
        now: Long
    ): List<PrayerTimesUiState.PrayerUiState> {
        // Implementation for prayer progress calculation
        return prayers
    }

    override fun onClickBack() {
        sendEffect(PrayerTimesEffect.NavigateBack)
    }

    fun onBatteryWarningClick() {
        updateState { it.copy(showBatteryDialog = true) }
    }

    fun onDismissBatteryDialog() {
        updateState { it.copy(showBatteryDialog = false) }
    }

    override fun onOpenSettingsClicked() {
        sendEffect(PrayerTimesEffect.RequestIgnoreBatteryOptimization)
        onDismissBatteryDialog()
    }

    override fun onSkipForNowClicked() {
        onDismissBatteryDialog()
    }

    override fun onBackClicked() {
        sendEffect(PrayerTimesEffect.NavigateBack)
    }

    override fun onLearnMoreClick() {
        // Open tutorial or help screen
    }

    override fun onClickEnablePrayer(prayerName: Prayer.PrayerName, isEnabled: Boolean) {
        viewModelScope.launch {
            if (isEnabled) {
                checkPermissionsBeforeEnable()
            }
            notificationsRepository.setPrayerEnabled(prayerName, isEnabled)
            scheduleAlarmsIfNeeded()
            analyticsHelper.logEvent(
                "prayer_notification_toggled",
                mapOf(
                    "prayer" to prayerName.name,
                    "enabled" to isEnabled.toString()
                )
            )
        }
    }

    /**
     * Checks for necessary system permissions before enabling a prayer alarm.
     */
    private fun checkPermissionsBeforeEnable() {
        if (!notificationScheduler.hasPermission()) {
            sendEffect(PrayerTimesEffect.RequestNotificationPermission)
            return
        }
        if (!alarmScheduler.hasPermission()) {
            sendEffect(PrayerTimesEffect.RequestExactAlarm)
            return
        }
        if (!batteryOptimizationRepository.isIgnoringBatteryOptimizations()) {
            sendEffect(PrayerTimesEffect.ShowBatteryOptimizationDialog)
            return
        }
    }
}
