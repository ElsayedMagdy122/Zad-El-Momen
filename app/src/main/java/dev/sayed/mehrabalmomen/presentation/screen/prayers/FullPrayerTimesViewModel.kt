@file:OptIn(ExperimentalTime::class)

package dev.sayed.mehrabalmomen.presentation.screen.prayers

import android.util.Log
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.data.AzanManager
import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.entity.Prayer
import dev.sayed.mehrabalmomen.domain.model.RescheduleResult
import dev.sayed.mehrabalmomen.domain.repository.PrayerNotificationsRepository
import dev.sayed.mehrabalmomen.domain.repository.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.utils.convertMillisToHMS
import dev.sayed.mehrabalmomen.presentation.utils.getTimeDifference
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class FullPrayerTimesViewModel(
    private val prayerRepository: PrayerRepository,
    private val settingsRepository: SettingsRepository,
    private val notificationsRepository: PrayerNotificationsRepository,
    private val azanManager: AzanManager
) : BaseViewModel<FullPrayerTimesUiState, FullPrayerTimesEffect>(FullPrayerTimesUiState()),
    FullPrayerTimeInteractionListener {
    private var countdownJob: Job? = null
    private val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

    init {
        observePrayerNotifications()
        getDailyPrayers()
        scheduleAlarmsIfNeeded()
    }

    private fun scheduleAlarmsIfNeeded() {
        viewModelScope.launch {
            combine(
                settingsRepository.observeAppSettings().distinctUntilChanged(),
                notificationsRepository.observeAll().distinctUntilChanged()
            ) { settings, notifications ->
                settings to notifications
            }
                .collect {
                    val result = azanManager.rescheduleTodayPrayerAlarms()
                    Log.d("AZAN_DEBUG", "Reschedule triggered $result")

                    if (result == RescheduleResult.PermissionRequired) {
                        sendEffect(FullPrayerTimesEffect.RequestExactAlarmPermission)
                    }
                }
        }
    }

    private fun getDailyPrayers() {
        tryToCall(
            block = ::getDailyPrayersBlock,
            onSuccess = ::onGetDailyPrayersSuccess,
            onError = {}
        )
    }

    private suspend fun getDailyPrayersBlock(): List<FullPrayerTimesUiState.PrayerUiState> {
        val settings = settingsRepository.observeAppSettings().first().prayerSettings
        val prayers = prayerRepository.getDailyPrayers(
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
        val zone = TimeZone.currentSystemDefault()
        return prayers.map { it.toPrayerUiState(zone = zone) }
    }

    private fun onGetDailyPrayersSuccess(prayers: List<FullPrayerTimesUiState.PrayerUiState>) {
        val now = Clock.System.now().toEpochMilliseconds()
        val updatedPrayers = calculatePrayerProgress(prayers, now)

        updateState { currentState ->
            currentState.copy(prayers = updatedPrayers)
        }
        getNextPrayer()
    }

    private fun getNextPrayer() {
        tryToCall(
            block = ::getNextPrayerBlock,
            onSuccess = ::onGetNextPrayerSuccess,
            onError = {
                updateState { currentState ->
                    currentState.copy(nextPrayer = FullPrayerTimesUiState.PrayerUiState())
                }
            }
        )
    }

    private suspend fun getNextPrayerBlock(): Prayer {
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
        return nextPrayer
    }

    private fun onGetNextPrayerSuccess(prayer: Prayer) {
        val zone = TimeZone.currentSystemDefault()
        val nextUi = prayer.toPrayerUiState(zone).copy(isUpComing = true)

        updateState { current ->
            val updatedList = current.prayers.map {
                it.copy(isUpComing = (it.name == nextUi.name))
            }

            current.copy(
                prayers = updatedList,
                nextPrayer = nextUi
            )

        }
        val nextInstantMillis = prayer.time.toEpochMilliseconds()
        startCountdown(nextInstantMillis)
    }

    private fun startCountdown(nextPrayerMillis: Long) {
        countdownJob?.cancel()

        countdownJob = viewModelScope.launch {
            while (true) {
                val diff = getTimeDifference(nextPrayerMillis)

                if (diff <= 0) {
                    handleCountdownFinished()
                    getNextPrayer()
                    break
                }

                val time = convertMillisToHMS(diff)
                updateCountdownUi(time)

                delay(1000)
            }
        }
    }

    private fun updateCountdownUi(time: Triple<String, String, String>) {
        updateState { current ->
            current.copy(
                time = FullPrayerTimesUiState.TimeUiState(
                    hours = time.first,
                    minutes = time.second,
                    seconds = time.third
                )
            )
        }
    }

    private fun handleCountdownFinished() {
        updateState { current ->
            current.copy(
                time = FullPrayerTimesUiState.TimeUiState("00", "00", "00")
            )
        }
    }

    private fun observePrayerNotifications() {
        viewModelScope.launch {
            notificationsRepository.observeAll().collect { map ->
                updateState { current ->
                    current.copy(
                        prayerNotifications = map.map { (prayer, enabled) ->
                            FullPrayerTimesUiState.PrayerNotificationUiState(
                                name = prayer.toStringRes(),
                                isEnabled = enabled
                            )
                        }
                    )
                }
            }
        }
    }

    fun Prayer.PrayerName.toStringRes(): Int = when (this) {
        Prayer.PrayerName.FAJR -> R.string.fajr
        Prayer.PrayerName.ZUHR -> R.string.dhuhr
        Prayer.PrayerName.ASR -> R.string.asr
        Prayer.PrayerName.MAGHRIB -> R.string.maghrib
        Prayer.PrayerName.ISHA -> R.string.isha
    }

    fun calculatePrayerProgress(
        prayers: List<FullPrayerTimesUiState.PrayerUiState>,
        nowMillis: Long
    ): List<FullPrayerTimesUiState.PrayerUiState> {
        val todayStartMillis = prayers.firstOrNull()?.instantTime
            ?.toLocalDateTime(TimeZone.currentSystemDefault())
            ?.date
            ?.atStartOfDayIn(TimeZone.currentSystemDefault())
            ?.toEpochMilliseconds() ?: 0L

        return prayers.mapIndexed { index, prayer ->
            val prayerTimeMillis = prayer.instantTime?.toEpochMilliseconds() ?: 0L
            val previousPrayerTimeMillisCorrected = if (index > 0) {
                prayers[index - 1].instantTime?.toEpochMilliseconds() ?: 0L
            } else {
                todayStartMillis
            }

            val progress = when {
                nowMillis >= prayerTimeMillis -> 1f
                nowMillis < previousPrayerTimeMillisCorrected -> 0f
                else -> {
                    val elapsed = nowMillis - previousPrayerTimeMillisCorrected
                    val duration = prayerTimeMillis - previousPrayerTimeMillisCorrected
                    (elapsed.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                }
            }

            prayer.copy(progress = progress)
        }
    }

    override fun onClickBack() {
        sendEffect(FullPrayerTimesEffect.NavigateBack)
    }

    override fun onClickEnablePrayer(
        prayerName: Prayer.PrayerName,
        isEnabled: Boolean
    ) {
        tryToCall(
            block = {
                notificationsRepository.setPrayerEnabled(
                    prayer = prayerName,
                    enabled = isEnabled
                )
            },
            onSuccess = {
            },
            onError = {}
        )
    }
}