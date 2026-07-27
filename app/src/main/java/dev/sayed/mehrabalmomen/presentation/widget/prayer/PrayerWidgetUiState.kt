package dev.sayed.mehrabalmomen.presentation.widget.prayer

import androidx.compose.runtime.Stable

@Stable
data class PrayerWidgetUiState(
    val status: PrayerWidgetStatus = PrayerWidgetStatus.LOADING,
    val nextPrayerName: String = "",
    val countdown: String = "00:00:00",
    val prayers: List<PrayerWidgetPrayer> = emptyList(),
    val isTomorrow: Boolean = false,
)
