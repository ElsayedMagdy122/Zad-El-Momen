package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.content.Context
import android.os.SystemClock
import android.widget.RemoteViews
import dev.sayed.mehrabalmomen.R

/**
 * Creates the XML RemoteViews fragment that hosts the launcher-driven countdown chronometer.
 *
 * @property currentTimeMillis wall-clock source used to compare the next-prayer epoch target with
 * the current epoch time.
 * @property elapsedRealtimeMillis elapsed-realtime source required by Android's Chronometer base.
 */
class PrayerWidgetCountdownRemoteViewsFactory(
    private val currentTimeMillis: () -> Long = { System.currentTimeMillis() },
    private val elapsedRealtimeMillis: () -> Long = { SystemClock.elapsedRealtime() },
) {
    /**
     * Builds a RemoteViews instance with a countdown chronometer configured for the target prayer.
     *
     * @param context Android context used to resolve the app package and layout resource.
     * @param targetEpochMillis absolute epoch-millis instant of the next prayer.
     * @return RemoteViews containing a started countdown chronometer.
     */
    fun create(
        context: Context,
        targetEpochMillis: Long?,
    ): RemoteViews {
        val remoteViews = RemoteViews(
            context.packageName,
            R.layout.prayer_widget_chronometer,
        )

        if (!canStart(targetEpochMillis)) return remoteViews

        remoteViews.setChronometer(
            R.id.prayer_widget_countdown_chronometer,
            chronometerBaseMillis(requireNotNull(targetEpochMillis)),
            null,
            true,
        )
        remoteViews.setChronometerCountDown(R.id.prayer_widget_countdown_chronometer, true)
        return remoteViews
    }

    /**
     * Checks whether a target can safely start a live countdown.
     *
     * @param targetEpochMillis absolute epoch-millis instant of the next prayer, if available.
     * @return `true` only when the target exists and is still in the future.
     */
    fun canStart(targetEpochMillis: Long?): Boolean {
        return targetEpochMillis != null && remainingMillis(targetEpochMillis) > 0L
    }

    /**
     * Converts a wall-clock prayer target to Android Chronometer's elapsed-realtime base.
     *
     * @param targetEpochMillis absolute epoch-millis instant of the next prayer.
     * @return elapsed-realtime base that makes Chronometer count down to the prayer target.
     */
    fun chronometerBaseMillis(targetEpochMillis: Long): Long {
        return elapsedRealtimeMillis() + remainingMillis(targetEpochMillis).coerceAtLeast(0L)
    }

    /**
     * Calculates how much wall-clock time remains until a target prayer.
     *
     * @param targetEpochMillis absolute epoch-millis instant of the next prayer.
     * @return positive, zero, or negative remaining milliseconds relative to [currentTimeMillis].
     */
    private fun remainingMillis(targetEpochMillis: Long): Long {
        return targetEpochMillis - currentTimeMillis()
    }
}
