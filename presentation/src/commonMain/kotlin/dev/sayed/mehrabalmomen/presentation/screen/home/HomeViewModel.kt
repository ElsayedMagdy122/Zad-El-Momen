@file:OptIn(ExperimentalTime::class)

package dev.sayed.mehrabalmomen.presentation.screen.home

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.ReadingProgressRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.domain.repository.notification.AppNotificationRepository
import dev.sayed.mehrabalmomen.domain.usecase.PrayerSchedulingUseCase
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.utils.convertMillisToHMS
import dev.sayed.mehrabalmomen.presentation.utils.getTimeDifference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class HomeViewModel(
    private val prayerRepository: PrayerRepository,
    private val readingProgressRepository: ReadingProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val quranRepository: QuranRepository,
    private val prayerSchedulingUseCase: PrayerSchedulingUseCase,
    private val permissionProvider: PermissionProvider,
    private val analyticsTracker: AnalyticsTracker,
    private val notificationRepository: AppNotificationRepository
) : BaseViewModel<HomeUiState, HomeEffect>(HomeUiState()), HomeInteractionListener {
    private var countdownJob: Job? = null
    private var isBottomSheetDismissedByUser = false
    private val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    private val _countdownTime = MutableStateFlow(HomeUiState.TimeUiState("00", "00", "00"))
    val countdownTime: StateFlow<HomeUiState.TimeUiState> = _countdownTime.asStateFlow()

    init {
        observeLocationChanges()
        observeContinueTilawah()
        checkPermissionsAndScheduleAlarms()
        observeUnreadNotifications()
    }

    private fun observeUnreadNotifications() {
        viewModelScope.launch {
            notificationRepository.getUnreadCount().collectLatest { unreadCount ->
                updateState { it.copy(unreadNotificationsCount = unreadCount) }
            }
        }
    }

    fun onScreenOpened() {
        checkPermissionsAndScheduleAlarms()
    }

    private fun checkPermissionsAndScheduleAlarms() {
        val isExactAlarmMissing = permissionProvider.isExactAlarmPermissionRequired() && !permissionProvider.canScheduleExactAlarms()
        val isNotificationMissing = permissionProvider.isNotificationPermissionRequired() && !permissionProvider.hasNotificationPermission()
        val isBatteryOptimizationMissing = !permissionProvider.isIgnoringBatteryOptimizations()
        val hasMissingPermissions = isExactAlarmMissing || isNotificationMissing || isBatteryOptimizationMissing

        if (hasMissingPermissions) {
            updateState {
                it.copy(
                    showAlarmPermissionBottomSheet = !isBottomSheetDismissedByUser,
                    isNotificationPermissionMissing = isNotificationMissing,
                    isExactAlarmPermissionMissing = isExactAlarmMissing,
                    isBatteryOptimizationMissing = isBatteryOptimizationMissing
                )
            }
        } else {
            isBottomSheetDismissedByUser = false
            updateState {
                it.copy(
                    showAlarmPermissionBottomSheet = false,
                    isNotificationPermissionMissing = false,
                    isExactAlarmPermissionMissing = false,
                    isBatteryOptimizationMissing = false
                )
            }
            scheduleAlarms()
        }
    }

    fun onGrantNotificationPermission() {
        isBottomSheetDismissedByUser = false
        sendEffect(HomeEffect.RequestNotificationPermission)
    }

    fun onGrantExactAlarmPermission() {
        isBottomSheetDismissedByUser = false
        sendEffect(HomeEffect.RequestExactAlarm)
    }

    fun onGrantBatteryPermission() {
        isBottomSheetDismissedByUser = false
        sendEffect(HomeEffect.RequestIgnoreBatteryOptimization)
    }

    fun onGrantAlarmPermissions() {
        isBottomSheetDismissedByUser = false
        val state = screenState.value
        when {
            state.isNotificationPermissionMissing -> sendEffect(HomeEffect.RequestNotificationPermission)
            state.isExactAlarmPermissionMissing -> sendEffect(HomeEffect.RequestExactAlarm)
            state.isBatteryOptimizationMissing -> sendEffect(HomeEffect.RequestIgnoreBatteryOptimization)
            else -> sendEffect(HomeEffect.RequestAlarmPermissions)
        }
    }

    fun onIgnoreAlarmPermissions() {
        isBottomSheetDismissedByUser = true
        updateState { it.copy(showAlarmPermissionBottomSheet = false) }
    }

    private fun scheduleAlarms() {
        viewModelScope.launch {
            try {
                prayerSchedulingUseCase.rescheduleTodayPrayerAlarms()
            } catch (_: Exception) {}
        }
    }

    private fun observeLocationChanges() {
        viewModelScope.launch {
            settingsRepository.observePrayerSettings().collect { prayerSettings ->
                updateLocationUi(prayerSettings.location)
                refreshPrayersForLocation(prayerSettings.location)
            }
        }
    }

    private fun observeContinueTilawah() {
        viewModelScope.launch {
            val surahs = quranRepository.getSurahs()
            val defaultSurah = surahs.firstOrNull { it.surahNumber == 1 }
            readingProgressRepository.observe().collect { data ->
                val targetSurahId = data?.surahId ?: 1
                val targetAyahId = data?.ayahId ?: 1
                val surah = surahs.firstOrNull { it.surahNumber == targetSurahId } ?: defaultSurah

                if (surah != null) {
                    updateState {
                        it.copy(
                            lastTilawahUi = HomeUiState.ContinueTilawahUi(
                                surahId = surah.surahNumber,
                                nameArabic = surah.nameArabic,
                                nameEnglish = surah.nameEnglish,
                                ayahId = targetAyahId
                            )
                        )
                    }
                }
            }
        }
    }

    private fun updateLocationUi(location: Location) {
        updateState {
            it.copy(
                location = HomeUiState.LocationUiState(
                    country = location.country,
                    city = location.state
                )
            )
        }
    }

    fun getHijriDate(language: AppSettings.Language) {
        val date = getHijriDateString(language)
        updateState {
            it.copy(hijriDate = date)
        }
    }

    private fun refreshPrayersForLocation(location: Location) {
        tryToCall(
            block = {
                val settings = settingsRepository
                    .observeAppSettings()
                    .first()
                    .prayerSettings

                val prayers = prayerRepository.getDailyPrayers(
                    madhab = settings.madhab,
                    calculationMethod = settings.calculationMethod,
                    location = location,
                    date = today
                )

                val zone = TimeZone.currentSystemDefault()
                prayers.map { it.toPrayerUiState(zone) }
            },
            onSuccess = { prayerList ->
                updateState {
                    it.copy(prayers = prayerList)
                }
                getNextPrayer()
            },
            onError = {}
        )
    }

    private fun getNextPrayer() {
        tryToCall(
            block = {
                val settings = settingsRepository.observeAppSettings().first().prayerSettings
                val nextPrayer = prayerRepository.getNextPrayer(
                    instant = Clock.System.now(),
                    madhab = settings.madhab,
                    calculationMethod = settings.calculationMethod,
                    location = Location(
                        longitude = settings.location.longitude,
                        latitude = settings.location.latitude,
                        country = "",
                        state = ""
                    ),
                    date = today
                )
                nextPrayer
            },
            onSuccess = { prayer ->
                val zone = TimeZone.currentSystemDefault()
                val nextUi = prayer.toPrayerUiState(zone).copy(isUpComing = true)

                updateState { current ->
                    val updatedList = current.prayers.map {
                        it.copy(isUpComing = (it.prayerName != null && it.prayerName == nextUi.prayerName))
                    }

                    current.copy(
                        prayers = updatedList,
                        nextPrayer = nextUi
                    )
                }
                val nextInstantMillis = prayer.time.toEpochMilliseconds()
                startCountdown(nextInstantMillis)
            },
            onError = {
                updateState { currentState ->
                    currentState.copy(nextPrayer = HomeUiState.PrayerUiState())
                }
            }
        )
    }

    private fun startCountdown(nextPrayerMillis: Long) {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                val diff = getTimeDifference(nextPrayerMillis)
                if (diff <= 0) {
                    _countdownTime.value = HomeUiState.TimeUiState("00", "00", "00")
                    getNextPrayer()
                    break
                }
                val time = convertMillisToHMS(diff)
                _countdownTime.value = HomeUiState.TimeUiState(
                    hours = time.first,
                    minutes = time.second,
                    seconds = time.third
                )
                delay(1000)
            }
        }
    }

    override fun onClickViewAll() {
        sendEffect(HomeEffect.NavigateToPrayerTimes)
    }

    override fun onClickSettings() {
        sendEffect(HomeEffect.NavigateToSettings)
    }

    override fun onClickQiblaDirection() {
        sendEffect(HomeEffect.NavigateToCalibrateDevice)
    }

    override fun onClickQuran() {
        analyticsTracker.logEvent(name = "on click surah list")
        sendEffect(HomeEffect.NavigateToQuran)
    }

    override fun onClickTilawah() {
        analyticsTracker.logEvent(name = "on click continue to tilawah")
        sendEffect(HomeEffect.NavigateToTilawah)
    }

    override fun onClickLocation() {
        sendEffect(HomeEffect.NavigateToLocation)
    }
}

expect fun getHijriDateString(language: AppSettings.Language): String
