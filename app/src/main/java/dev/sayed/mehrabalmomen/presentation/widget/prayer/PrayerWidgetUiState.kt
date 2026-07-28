package dev.sayed.mehrabalmomen.presentation.widget.prayer

import androidx.compose.runtime.Stable

/**
 * Fully formatted presentation state consumed by the prayer widget renderer.
 *
 * @property status high-level state that selects content, setup, permission, or error UI.
 * @property nextPrayerName localized name of the next prayer.
 * @property nextPrayerTime localized 12-hour time of the next prayer.
 * @property countdown formatted remaining duration captured when this state was mapped.
 * @property countdownStartEpochMillis absolute previous-prayer boundary used for ring progress.
 * @property targetEpochMillis absolute next-prayer target used for a live countdown.
 * @property countdownProgress ring fill in the inclusive `0..10000` widget progress range.
 * @property displayedDate localized date represented by [prayers].
 * @property timeZoneId timezone used when formatting prayer times.
 * @property languageCode saved language code used for presentation.
 * @property isRtl whether the widget should use a right-to-left layout direction.
 * @property prayers ordered, localized prayer rows displayed by the widget.
 * @property isTomorrow whether [displayedDate] is later than the calculation's current date.
 */
@Stable
data class PrayerWidgetUiState(
    val status: PrayerWidgetStatus = PrayerWidgetStatus.LOADING,
    val nextPrayerName: String = "",
    val nextPrayerTime: String = "",
    val countdown: String = "00:00:00",
    val countdownStartEpochMillis: Long? = null,
    val targetEpochMillis: Long? = null,
    val countdownProgress: Int = 0,
    val displayedDate: String = "",
    val timeZoneId: String = "",
    val languageCode: String = "",
    val isRtl: Boolean = false,
    val prayers: List<PrayerWidgetPrayer> = emptyList(),
    val isTomorrow: Boolean = false,
)
