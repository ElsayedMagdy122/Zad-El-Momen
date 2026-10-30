package dev.sayed.mehrabalmomen.presentation.screen.azan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar

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
    private val settingsRepository: SettingsRepository
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
        val cal = Calendar.getInstance()
        val isFriday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
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
        val formatted = formatTime(prayerTimeMillis)
        val nextPrayer = getNextPrayerEnum(prayerName)
        val nextTimeMillis = prayerTimeMillis + (3 * 60 * 60 * 1000L) + (28 * 60 * 1000L)
        val nextFormatted = formatTime(nextTimeMillis)

        _state.update {
            it.copy(
                prayerName = prayerName,
                prayerTimeMillis = prayerTimeMillis,
                prayerTimeFormatted = formatted.first,
                isAm = formatted.second,
                nextPrayerName = nextPrayer,
                nextPrayerTimeFormatted = nextFormatted.first,
                nextPrayerIsAm = nextFormatted.second
            )
        }
    }

    fun toggleMute() {
        _state.update { it.copy(isMuted = !it.isMuted) }
    }

    private fun getNextPrayerEnum(current: Prayer.PrayerName): Prayer.PrayerName = when (current) {
        Prayer.PrayerName.FAJR -> Prayer.PrayerName.ZUHR
        Prayer.PrayerName.ZUHR -> Prayer.PrayerName.ASR
        Prayer.PrayerName.ASR -> Prayer.PrayerName.MAGHRIB
        Prayer.PrayerName.MAGHRIB -> Prayer.PrayerName.ISHA
        Prayer.PrayerName.ISHA -> Prayer.PrayerName.FAJR
    }

    private fun formatTime(millis: Long): Pair<String, Boolean> {
        val cal = Calendar.getInstance().apply { timeInMillis = millis }
        val hour = cal.get(Calendar.HOUR)
        val minute = cal.get(Calendar.MINUTE)
        val isAm = cal.get(Calendar.AM_PM) == Calendar.AM
        val hourStr = if (hour == 0) "12" else hour.toString().padStart(2, '0')
        val minStr = minute.toString().padStart(2, '0')
        return "$hourStr:$minStr" to isAm
    }
}
