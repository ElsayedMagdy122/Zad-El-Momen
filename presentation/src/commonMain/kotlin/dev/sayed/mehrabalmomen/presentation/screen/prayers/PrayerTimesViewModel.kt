package dev.sayed.mehrabalmomen.presentation.screen.prayers

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerNotificationsRepository
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.BatteryOptimizationRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import dev.sayed.mehrabalmomen.domain.utils.Logger
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.BatteryOptimizationInteractionListener
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
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
    private val permissionProvider: PermissionProvider,
    private val analyticsTracker: AnalyticsTracker,
    private val logger: Logger,
    private val audioPlayer: AudioPlayer
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
        observeSelectedMoazen()
        refreshBatteryStatus()
        checkFirstTimeBatteryDialog()
    }

    private fun observeSelectedMoazen() {
        viewModelScope.launch {
            settingsRepository.observeSelectedMoazen().collect { fileName ->
                val moazen = SettingsUiState.Moazen.entries.firstOrNull { it.fileName == fileName }
                    ?: SettingsUiState.Moazen.AZAN_MAKKAH
                updateState { it.copy(selectedMoazen = moazen) }
            }
        }
    }

    fun openMoazenDialog() {
        updateState { it.copy(showMoazenDialog = true) }
    }

    fun dismissMoazenDialog() {
        stopPreview()
        updateState { it.copy(showMoazenDialog = false) }
    }

    fun playPreview(index: Int) {
        stopPreview()
        val moazen = SettingsUiState.Moazen.entries.getOrNull(index) ?: return
        val audioName = moazen.fileName.removeSuffix(".mp3")
        audioPlayer.play(AudioSource.LocalResource(audioName))
        updateState { it.copy(playingMoazenPreview = moazen) }
    }

    fun stopPreview() {
        audioPlayer.stop()
        updateState { it.copy(playingMoazenPreview = null) }
    }

    fun onConfirmMoazen(index: Int) {
        stopPreview()
        val selected = SettingsUiState.Moazen.entries.getOrNull(index) ?: return
        updateState { it.copy(selectedMoazen = selected, showMoazenDialog = false) }
        viewModelScope.launch {
            settingsRepository.saveSelectedMoazen(selected.fileName)
        }
    }

    private fun checkFirstTimeBatteryDialog() {
        viewModelScope.launch {
            val hasSeen = settingsRepository.observeHasSeenBatteryDialog().first()
            if (!hasSeen) {
                refreshBatteryStatus()
                updateState { it.copy(showBatteryDialog = true) }
                settingsRepository.setHasSeenBatteryDialog(true)
            }
        }
    }

    fun refreshBatteryStatus() {
        val isIgnoring = batteryOptimizationRepository.isIgnoringBatteryOptimizations()
        val hasOverlay = permissionProvider.canDrawOverlays()
        viewModelScope.launch {
            try {
                val language = settingsRepository.observeAppSettings().first().language
                val isArabic = language == AppSettings.Language.ARABIC
                val instructions = batteryOptimizationRepository.getBrandInstructions(
                    manufacturer = "Android",
                    isArabic = isArabic
                )
                updateState {
                    it.copy(
                        isBatteryOptimizationEnabled = !isIgnoring,
                        isOverlayPermissionGranted = hasOverlay,
                        batteryInstructions = instructions
                    )
                }
            } catch (_: Exception) {
            }
        }
    }

    private var isBottomSheetDismissedByUser = false

    fun onScreenOpened() {
        getDailyPrayers()
        getNextPrayer()
        checkPermissionsAndScheduleAlarms()
    }

    private fun checkPermissionsAndScheduleAlarms() {
        val isExactAlarmMissing = permissionProvider.isExactAlarmPermissionRequired() && !permissionProvider.canScheduleExactAlarms()
        val isNotificationMissing = permissionProvider.isNotificationPermissionRequired() && !permissionProvider.hasNotificationPermission()
        val isBatteryOptimizationMissing = !permissionProvider.isIgnoringBatteryOptimizations()
        val hasMissingPermissions = isExactAlarmMissing || isNotificationMissing || isBatteryOptimizationMissing

        if (!hasMissingPermissions) {
            isBottomSheetDismissedByUser = false
            updateState {
                it.copy(
                    showAlarmPermissionBottomSheet = false,
                    isNotificationPermissionMissing = false,
                    isExactAlarmPermissionMissing = false,
                    isBatteryOptimizationMissing = false
                )
            }
            scheduleAlarmsIfNeeded()
        } else {
            updateState {
                it.copy(
                    isNotificationPermissionMissing = isNotificationMissing,
                    isExactAlarmPermissionMissing = isExactAlarmMissing,
                    isBatteryOptimizationMissing = isBatteryOptimizationMissing
                )
            }
        }
    }

    fun onGrantNotificationPermission() {
        isBottomSheetDismissedByUser = false
        sendEffect(PrayerTimesEffect.RequestNotificationPermission)
    }

    fun onGrantExactAlarmPermission() {
        isBottomSheetDismissedByUser = false
        sendEffect(PrayerTimesEffect.RequestExactAlarm)
    }

    fun onGrantBatteryPermission() {
        isBottomSheetDismissedByUser = false
        sendEffect(PrayerTimesEffect.RequestIgnoreBatteryOptimization)
    }

    fun onGrantAlarmPermissions() {
        isBottomSheetDismissedByUser = false
        val state = screenState.value
        if (state.isNotificationPermissionMissing) {
            sendEffect(PrayerTimesEffect.RequestNotificationPermission)
        } else if (state.isExactAlarmPermissionMissing) {
            sendEffect(PrayerTimesEffect.RequestExactAlarm)
        } else if (state.isBatteryOptimizationMissing) {
            sendEffect(PrayerTimesEffect.RequestIgnoreBatteryOptimization)
        }
    }

    fun onIgnoreAlarmPermissions() {
        isBottomSheetDismissedByUser = true
        updateState { it.copy(showAlarmPermissionBottomSheet = false) }
    }

    private fun scheduleAlarmsIfNeeded() {
        viewModelScope.launch {
            prayerSchedulingUseCase.rescheduleTodayPrayerAlarms()
        }
    }

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

                val isExactAlarmMissing = permissionProvider.isExactAlarmPermissionRequired() && !permissionProvider.canScheduleExactAlarms()
                val isNotificationMissing = permissionProvider.isNotificationPermissionRequired() && !permissionProvider.hasNotificationPermission()
                val isBatteryOptimizationMissing = !permissionProvider.isIgnoringBatteryOptimizations()
                val hasMissingPermissions = isExactAlarmMissing || isNotificationMissing || isBatteryOptimizationMissing

                val prayerUiStates = prayers.map { prayer ->
                    prayer.toPrayerUiState(zone).copy(
                        isNotificationEnabled = if (hasMissingPermissions) false else (notifications[prayer.name] ?: true),
                        isUpComing = prayer.name == nextPrayer.name
                    )
                }

                updateState {
                    it.copy(
                        prayers = prayerUiStates
                    )
                }
            } catch (e: Exception) {
                logger.e("PrayerTimesVM", "Error getting daily prayers", e)
            }
        }
    }

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

    private fun startCountdown(targetMillis: Long) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (isActive) {
                val now = Clock.System.now().toEpochMilliseconds()
                val diff = targetMillis - now
                if (diff <= 0) {
                    getNextPrayer()
                    break
                }
                val hours = (diff / (1000 * 60 * 60)).toString().padStart(2, '0')
                val minutes = ((diff / (1000 * 60)) % 60).toString().padStart(2, '0')
                val seconds = ((diff / 1000) % 60).toString().padStart(2, '0')
                _countdownTime.value = PrayerTimesUiState.TimeUiState(
                    hours = hours,
                    minutes = minutes,
                    seconds = seconds
                )
                delay(1.seconds)
            }
        }
    }

    private fun observePrayerNotifications() {
        notificationsRepository.observeAll()
            .onEach {
                getDailyPrayers()
            }
            .launchIn(viewModelScope)
    }

    override fun onClickBack() {
        sendEffect(PrayerTimesEffect.NavigateBack)
    }

    fun onBatteryWarningClick() {
        refreshBatteryStatus()
        updateState { it.copy(showBatteryDialog = true) }
    }

    fun onDismissBatteryDialog() {
        updateState { it.copy(showBatteryDialog = false) }
    }

    override fun onOpenSettingsClicked() {
        sendEffect(PrayerTimesEffect.OpenAppSettings)
        onDismissBatteryDialog()
    }

    override fun onOpenOverlaySettingsClicked() {
        sendEffect(PrayerTimesEffect.RequestOverlayPermission)
    }

    override fun onSkipForNowClicked() {
        onDismissBatteryDialog()
    }

    override fun onBackClicked() {
        sendEffect(PrayerTimesEffect.NavigateBack)
    }

    override fun onLearnMoreClick() {
    }

    override fun onClickEnablePrayer(prayerName: Prayer.PrayerName, isEnabled: Boolean) {
        viewModelScope.launch {
            if (isEnabled) {
                val isExactAlarmMissing = permissionProvider.isExactAlarmPermissionRequired() && !permissionProvider.canScheduleExactAlarms()
                val isNotificationMissing = permissionProvider.isNotificationPermissionRequired() && !permissionProvider.hasNotificationPermission()
                val isBatteryOptimizationMissing = !permissionProvider.isIgnoringBatteryOptimizations()
                val hasMissingPermissions = isExactAlarmMissing || isNotificationMissing || isBatteryOptimizationMissing

                if (hasMissingPermissions) {
                    isBottomSheetDismissedByUser = false
                    updateState {
                        it.copy(
                            showAlarmPermissionBottomSheet = true,
                            isNotificationPermissionMissing = isNotificationMissing,
                            isExactAlarmPermissionMissing = isExactAlarmMissing,
                            isBatteryOptimizationMissing = isBatteryOptimizationMissing
                        )
                    }
                    return@launch
                }
            }
            notificationsRepository.setPrayerEnabled(prayerName, isEnabled)
            scheduleAlarmsIfNeeded()
            analyticsTracker.logEvent(
                "prayer_notification_toggled",
                mapOf(
                    "prayer" to prayerName.name,
                    "enabled" to isEnabled.toString()
                )
            )
        }
    }


}
