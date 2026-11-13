package dev.sayed.mehrabalmomen.presentation.screen.azan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.domain.utils.getCurrentTimeMillis
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class AzanUiState(
    val prayerName: Prayer.PrayerName = Prayer.PrayerName.ZUHR,
    val prayerTimeMillis: Long = 0L,
    val prayerTimeFormatted: String = "12:20",
    val isAm: Boolean = false,
    val isFriday: Boolean = false,
    val location: String = "",
    val elapsedSeconds: Long = 0L,
    val formattedElapsed: String = "00:00",
    val nextPrayerName: Prayer.PrayerName = Prayer.PrayerName.ASR,
    val nextPrayerTimeFormatted: String = "03:48",
    val nextPrayerIsAm: Boolean = false,
    val isMuted: Boolean = false
)

class AzanViewModel(
    private val settingsRepository: SettingsRepository,
    private val prayerRepository: PrayerRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AzanUiState())
    val state: StateFlow<AzanUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val location = settingsRepository.observeLocation().first()
            val locationText = location.state.ifBlank { location.country }
            _state.update { it.copy(location = locationText) }
        }
        checkFriday()
        startElapsedTimer()
    }

    private fun checkFriday() {
        val now = Instant.fromEpochMilliseconds(getCurrentTimeMillis()).toLocalDateTime(TimeZone.currentSystemDefault())
        val isFriday = now.dayOfWeek == DayOfWeek.FRIDAY
        _state.update { it.copy(isFriday = isFriday) }
    }

    private fun startElapsedTimer() {
        viewModelScope.launch {
            while (isActive) {
                delay(1000L)
                _state.update { state ->
                    val newElapsed = state.elapsedSeconds + 1L
                    val mins = (newElapsed / 60).toString().padStart(2, '0')
                    val secs = (newElapsed % 60).toString().padStart(2, '0')
                    state.copy(
                        elapsedSeconds = newElapsed,
                        formattedElapsed = "$mins:$secs"
                    )
                }
            }
        }
    }

    fun setPrayerInfo(prayerName: Prayer.PrayerName, prayerTimeMillis: Long) {
        val baseMillis = if (prayerTimeMillis > 0L) prayerTimeMillis else getCurrentTimeMillis()
        val formatted = formatTime(baseMillis)

        _state.update {
            it.copy(
                prayerName = prayerName,
                prayerTimeMillis = baseMillis,
                prayerTimeFormatted = formatted.first,
                isAm = formatted.second
            )
        }

        viewModelScope.launch {
            try {
                val appSettings = settingsRepository.observeAppSettings().first()
                val prayerSettings = appSettings.prayerSettings
                val currentInstant = Instant.fromEpochMilliseconds(baseMillis + 1000L)
                val date = currentInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date

                val nextPrayer = prayerRepository.getNextPrayer(
                    instant = currentInstant,
                    madhab = prayerSettings.madhab,
                    calculationMethod = prayerSettings.calculationMethod,
                    location = Location(
                        latitude = prayerSettings.location.latitude,
                        longitude = prayerSettings.location.longitude
                    ),
                    date = date
                )

                val nextTimeMillis = nextPrayer.time.toEpochMilliseconds()
                val nextFormatted = formatTime(nextTimeMillis)

                _state.update {
                    it.copy(
                        nextPrayerName = nextPrayer.name,
                        nextPrayerTimeFormatted = nextFormatted.first,
                        nextPrayerIsAm = nextFormatted.second
                    )
                }
            } catch (_: Exception) {
                val fallbackNext = getNextPrayerEnum(prayerName)
                _state.update { it.copy(nextPrayerName = fallbackNext) }
            }
        }
    }

    fun toggleMute(): Boolean {
        val newMuteState = !_state.value.isMuted
        _state.update { it.copy(isMuted = newMuteState) }
        return newMuteState
    }

    private fun getNextPrayerEnum(current: Prayer.PrayerName): Prayer.PrayerName = when (current) {
        Prayer.PrayerName.FAJR -> Prayer.PrayerName.ZUHR
        Prayer.PrayerName.ZUHR -> Prayer.PrayerName.ASR
        Prayer.PrayerName.ASR -> Prayer.PrayerName.MAGHRIB
        Prayer.PrayerName.MAGHRIB -> Prayer.PrayerName.ISHA
        Prayer.PrayerName.ISHA -> Prayer.PrayerName.FAJR
    }

    private fun formatTime(millis: Long): Pair<String, Boolean> {
        val ldt = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault())
        val rawHour = ldt.hour
        val minute = ldt.minute
        val isAm = rawHour < 12
        val hour12 = when (rawHour) {
            0 -> 12
            in 1..12 -> rawHour
            else -> rawHour - 12
        }
        val hourStr = hour12.toString().padStart(2, '0')
        val minStr = minute.toString().padStart(2, '0')
        return "$hourStr:$minStr" to isAm
    }
}
