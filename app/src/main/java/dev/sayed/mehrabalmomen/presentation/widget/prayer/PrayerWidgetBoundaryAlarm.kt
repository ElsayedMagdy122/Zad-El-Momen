package dev.sayed.mehrabalmomen.presentation.widget.prayer

/** Platform boundary for exact alarms that advance every installed prayer widget. */
interface PrayerWidgetBoundaryAlarm {
    /**
     * Replaces the current widget prayer-boundary alarm with one targeting the supplied time.
     *
     * @param targetEpochMillis absolute epoch-millis prayer boundary to schedule.
     * @return `true` when the platform accepted the alarm, or `false` when access was revoked.
     */
    fun schedulePrayerBoundary(targetEpochMillis: Long): Boolean

    /**
     * Replaces the current widget local-midnight alarm with one targeting the supplied time.
     *
     * @param targetEpochMillis absolute epoch-millis local midnight instant to schedule.
     * @return `true` when the platform accepted the alarm, or `false` when access was revoked.
     */
    fun scheduleLocalMidnight(targetEpochMillis: Long): Boolean

    /**
     * Cancels all widget-only exact alarms without touching Azan notification alarms.
     *
     * @return no value; after completion, stale widget boundary and midnight alarms are removed.
     */
    fun cancelAll()
}
