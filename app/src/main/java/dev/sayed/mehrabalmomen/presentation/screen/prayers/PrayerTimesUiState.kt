package dev.sayed.mehrabalmomen.presentation.screen.prayers

import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.presentation.base.UiIcon
import dev.sayed.mehrabalmomen.presentation.base.UiText
import dev.sayed.mehrabalmomen.presentation.utils.FormattedTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@ExperimentalTime
data class PrayerTimesUiState(
    val time: TimeUiState = TimeUiState(),
    val prayers: List<PrayerUiState> = emptyList(),
    val nextPrayer: PrayerUiState = PrayerUiState(),
    val prayerNotifications: List<PrayerNotificationUiState> = emptyList(),
    val isBatteryOptimizationEnabled: Boolean = false,
    val showBatteryDialog: Boolean = false,
    val batteryInstructions: List<String> = emptyList()
) {
    data class PrayerUiState(
        val prayerName: Prayer.PrayerName = Prayer.PrayerName.FAJR,
        val name: UiText = UiText.DynamicString(""),
        val time: FormattedTime = FormattedTime(time = "00:00", isAm = false),
        val isUpComing: Boolean = false,
        val progress: Float = 100.0f,
        val icon: UiIcon = UiIcon(0),
        val instantTime: Instant? = null,
        val isNotificationEnabled: Boolean = false
    )

    data class TimeUiState(
        val hours: String = "00",
        val minutes: String = "00",
        val seconds: String = "00",
    )

    data class PrayerNotificationUiState(
        val prayerName: Prayer.PrayerName = Prayer.PrayerName.FAJR,
        val name: UiText = UiText.DynamicString(""),
        val isEnabled: Boolean = true
    )
}
