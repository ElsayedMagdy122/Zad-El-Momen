package dev.sayed.mehrabalmomen.presentation.widget.prayer

/** Platform boundary for the single exact alarm that advances every installed prayer widget. */
interface PrayerWidgetBoundaryAlarm {
    /**
     * Replaces the current widget boundary alarm with one targeting the supplied wall-clock time.
     *
     * @param targetEpochMillis absolute epoch-millis prayer boundary to schedule.
     * @return `true` when the platform accepted the alarm, or `false` when access was revoked.
     */
    fun schedule(targetEpochMillis: Long): Boolean

    /** Cancels the currently registered widget prayer-boundary alarm, if one exists. */
    fun cancel()
}
