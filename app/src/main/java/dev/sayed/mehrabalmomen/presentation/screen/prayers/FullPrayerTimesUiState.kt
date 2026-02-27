package dev.sayed.mehrabalmomen.presentation.screen.prayers

import androidx.annotation.StringRes
import dev.sayed.mehrabalmomen.presentation.utils.FormattedTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@ExperimentalTime
data class FullPrayerTimesUiState(
    val time: TimeUiState = TimeUiState(),
    val prayers: List<PrayerUiState> = emptyList(),
    val nextPrayer: PrayerUiState = PrayerUiState(),
    val prayerNotifications: List<PrayerNotificationUiState> =emptyList()
) {
    data class PrayerUiState(
        @param:StringRes
        val name: Int = 0,
        val time: FormattedTime = FormattedTime(time = "00:00",isAm = false),
        val isUpComing: Boolean = false,
        val progress: Float = 100.0f,
        val icon: Int = 0,
        val instantTime: Instant? = null,
        val isNotificationEnabled: Boolean = false
    )

    data class TimeUiState(
        val hours: String = "00",
        val minutes: String = "00",
        val seconds: String = "00",
    )

    data class PrayerNotificationUiState(
        @param:StringRes
        val name: Int = 0,
        val isEnabled: Boolean = true
    )

}