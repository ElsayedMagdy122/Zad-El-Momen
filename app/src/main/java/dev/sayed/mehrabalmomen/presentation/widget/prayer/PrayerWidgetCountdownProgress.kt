package dev.sayed.mehrabalmomen.presentation.widget.prayer

/** Maximum progress value used when rendering a completely filled countdown ring. */
internal const val PRAYER_WIDGET_COUNTDOWN_MAX_PROGRESS = 10_000

/**
 * Calculates how much of a prayer-to-prayer countdown interval has elapsed.
 *
 * @param startEpochMillis absolute epoch-millis instant at which the interval began, if known.
 * @param targetEpochMillis absolute epoch-millis instant at which the interval completes, if known.
 * @param currentEpochMillis absolute epoch-millis instant represented by the current snapshot.
 * @return progress clamped to `0..10000`, or zero when either boundary is missing or invalid.
 */
internal fun calculatePrayerWidgetCountdownProgress(
    startEpochMillis: Long?,
    targetEpochMillis: Long?,
    currentEpochMillis: Long,
): Int {
    if (startEpochMillis == null || targetEpochMillis == null) return 0
    val totalMillis = targetEpochMillis - startEpochMillis
    if (totalMillis <= 0L) return 0

    val elapsedMillis = (currentEpochMillis - startEpochMillis).coerceIn(0L, totalMillis)
    return ((elapsedMillis.toDouble() / totalMillis) * PRAYER_WIDGET_COUNTDOWN_MAX_PROGRESS)
        .toInt()
        .coerceIn(0, PRAYER_WIDGET_COUNTDOWN_MAX_PROGRESS)
}

/**
 * Converts the widget's integer progress value into a clockwise ring sweep angle.
 *
 * @param progress progress value that may fall inside or outside the supported range.
 * @return sweep angle clamped from zero degrees through a complete 360-degree circle.
 */
internal fun prayerWidgetCountdownSweepAngle(progress: Int): Float {
    val normalizedProgress = progress.coerceIn(0, PRAYER_WIDGET_COUNTDOWN_MAX_PROGRESS)
    return normalizedProgress.toFloat() / PRAYER_WIDGET_COUNTDOWN_MAX_PROGRESS * 360f
}
