package dev.sayed.mehrabalmomen.presentation.screen.prayers

import androidx.annotation.StringRes
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@ExperimentalTime
data class FullPrayerTimesUiState(
    val time: TimeUiState = TimeUiState(),
    val prayers: List<PrayerUiState> = emptyList(),
    val nextPrayer: PrayerUiState = PrayerUiState()
) {
    data class PrayerUiState(
        @param:StringRes
        val name: Int = 0,
        val time: String = "00 : 00 : 00",
        val isUpComing: Boolean = false,
        val progress : Float = 100.0f,
        val icon: Int = 0,
        val instantTime: Instant? = null
    )

    data class TimeUiState(
        val hours: String = "00",
        val minutes: String = "00",
        val seconds: String = "00",
    )
}
