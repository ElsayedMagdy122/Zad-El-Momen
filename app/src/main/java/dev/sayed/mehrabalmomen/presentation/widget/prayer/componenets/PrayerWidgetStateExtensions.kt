package dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets

import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetPrayer
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetUiState

internal fun PrayerWidgetUiState.upcomingPrayerIndex(): Int {
    if (prayers.isEmpty()) return -1

    val markedIndex = prayers.indexOfFirst { it.isUpcoming }
    if (markedIndex >= 0) return markedIndex

    val namedIndex = prayers.indexOfFirst { it.name == nextPrayerName }
    return if (namedIndex >= 0) namedIndex else 0
}

internal fun PrayerWidgetUiState.upcomingPrayer(): PrayerWidgetPrayer? =
    prayers.getOrNull(upcomingPrayerIndex())

internal fun PrayerWidgetUiState.currentPrayer(): PrayerWidgetPrayer? {
    if (prayers.isEmpty()) return null
    val currentIndex = (upcomingPrayerIndex() - 1 + prayers.size) % prayers.size
    return prayers[currentIndex]
}

internal fun PrayerWidgetUiState.followingPrayer(): PrayerWidgetPrayer? {
    if (prayers.isEmpty()) return null
    val followingIndex = (upcomingPrayerIndex() + 1) % prayers.size
    return prayers[followingIndex]
}
