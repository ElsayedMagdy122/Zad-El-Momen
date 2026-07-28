package dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets

import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetPrayer
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetUiState

/**
 * Finds the index of the prayer row that represents the next upcoming prayer.
 *
 * @receiver widget state containing the ordered prayer rows and next-prayer display name.
 * @return the upcoming prayer index, or `-1` when the state has no prayer rows.
 */
internal fun PrayerWidgetUiState.upcomingPrayerIndex(): Int {
    if (prayers.isEmpty()) return -1

    val markedIndex = prayers.indexOfFirst { it.isUpcoming }
    if (markedIndex >= 0) return markedIndex

    val namedIndex = prayers.indexOfFirst { it.name == nextPrayerName }
    return if (namedIndex >= 0) namedIndex else 0
}

/**
 * Returns the prayer row marked as the next upcoming prayer.
 *
 * @receiver widget state containing the ordered prayer rows.
 * @return the upcoming prayer row, or `null` when no row can be resolved.
 */
internal fun PrayerWidgetUiState.upcomingPrayer(): PrayerWidgetPrayer? =
    prayers.getOrNull(upcomingPrayerIndex())

/**
 * Returns the latest prayer before the upcoming prayer without wrapping across days.
 *
 * @receiver widget state containing the ordered prayer rows for the displayed date.
 * @return the current or previous prayer row, or `null` before the first displayed prayer.
 */
internal fun PrayerWidgetUiState.currentPrayer(): PrayerWidgetPrayer? {
    if (prayers.isEmpty()) return null
    val currentIndex = upcomingPrayerIndex() - 1
    return prayers.getOrNull(currentIndex)
}

/**
 * Returns the prayer after the upcoming prayer without wrapping across days.
 *
 * @receiver widget state containing the ordered prayer rows for the displayed date.
 * @return the following prayer row, or `null` when the upcoming prayer is the final row.
 */
internal fun PrayerWidgetUiState.followingPrayer(): PrayerWidgetPrayer? {
    if (prayers.isEmpty()) return null
    val followingIndex = upcomingPrayerIndex() + 1
    return prayers.getOrNull(followingIndex)
}
