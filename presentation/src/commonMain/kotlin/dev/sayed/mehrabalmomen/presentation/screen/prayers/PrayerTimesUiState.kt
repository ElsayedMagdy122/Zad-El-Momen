package dev.sayed.mehrabalmomen.presentation.screen.prayers

import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.presentation.base.UiIcon
import dev.sayed.mehrabalmomen.presentation.base.UiText
import dev.sayed.mehrabalmomen.presentation.utils.FormattedTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.ic_azan_selected

@ExperimentalTime
data class PrayerTimesUiState(
    val time: TimeUiState = TimeUiState(),
    val prayers: List<PrayerUiState> = emptyList(),
    val nextPrayer: PrayerUiState = PrayerUiState(),
    val prayerNotifications: List<PrayerNotificationUiState> = emptyList(),
    val isBatteryOptimizationEnabled: Boolean = false,
    val isOverlayPermissionGranted: Boolean = true,
    val showBatteryDialog: Boolean = false,
    val batteryInstructions: List<String> = emptyList()
) {
    data class PrayerUiState(
        val prayerName: Prayer.PrayerName = Prayer.PrayerName.FAJR,
        val name: UiText = UiText.DynamicString(""),
        val time: FormattedTime = FormattedTime(time = "00:00", isAm = false),
        val isUpComing: Boolean = false,
        val progress: Float = 100.0f,
        val icon: UiIcon = UiIcon(Res.drawable.ic_azan_selected),
        val instantTime: Instant? = null,
        val isNotificationEnabled: Boolean = false
    )

    data class TimeUiState(
        val hours: String = "00",
        val minutes: String = "00",
        val seconds: String = "00",
        val amPm: String = "",
        val hijriDate: String = "",
        val gregorianDate: String = "",
        val nextPrayerName: String = "",
        val remainingTime: String = ""
    )

    data class PrayerNotificationUiState(
        val prayerName: Prayer.PrayerName,
        val name: UiText,
        val isEnabled: Boolean,
        val adhanType: AdhanType
    )

    enum class AdhanType {
        MUTE, NOTIFICATION, ADHAN
    }
}
