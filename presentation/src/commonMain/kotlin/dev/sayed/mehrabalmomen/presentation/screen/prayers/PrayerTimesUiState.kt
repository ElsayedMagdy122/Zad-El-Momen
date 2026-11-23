package dev.sayed.mehrabalmomen.presentation.screen.prayers

import dev.sayed.mehrabalmomen.design_system.base.UiText
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsUiState
import dev.sayed.mehrabalmomen.presentation.utils.FormattedTime
import org.jetbrains.compose.resources.DrawableResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.fajr
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class PrayerTimesUiState(
    val time: TimeUiState = TimeUiState(),
    val prayers: List<PrayerUiState> = emptyList(),
    val nextPrayer: PrayerUiState = PrayerUiState(),
    val prayerNotifications: List<PrayerNotificationUiState> = emptyList(),
    val isBatteryOptimizationEnabled: Boolean = false,
    val isOverlayPermissionGranted: Boolean = true,
    val showBatteryDialog: Boolean = false,
    val batteryInstructions: List<String> = emptyList(),
    val selectedMoazen: SettingsUiState.Moazen = SettingsUiState.Moazen.AZAN_MAKKAH,
    val showMoazenDialog: Boolean = false,
    val playingMoazenPreview: SettingsUiState.Moazen? = null,
    val showAlarmPermissionBottomSheet: Boolean = false,
    val isNotificationPermissionMissing: Boolean = false,
    val isExactAlarmPermissionMissing: Boolean = false,
    val isBatteryOptimizationMissing: Boolean = false
) {
    data class PrayerUiState(
        val prayerName: Prayer.PrayerName = Prayer.PrayerName.FAJR,
        val name: UiText = UiText.DynamicString(""),
        val time: FormattedTime = FormattedTime(time = "00:00", isAm = false),
        val isUpComing: Boolean = false,
        val progress: Float = 100.0f,
        val icon: DrawableResource = Res.drawable.fajr,
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
