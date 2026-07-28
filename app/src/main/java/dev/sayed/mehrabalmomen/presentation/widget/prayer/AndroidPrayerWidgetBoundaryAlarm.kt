package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/**
 * AlarmManager implementation of the single prayer-widget boundary alarm.
 *
 * @property context application context used to access AlarmManager and create the explicit intent.
 */
class AndroidPrayerWidgetBoundaryAlarm(
    private val context: Context,
) : PrayerWidgetBoundaryAlarm {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    /**
     * Schedules an exact idle-capable broadcast at the next prayer boundary.
     *
     * A stable PendingIntent identity makes every call replace the previous widget-only alarm.
     * A permission race is converted into `false` instead of allowing SecurityException to crash.
     *
     * @param targetEpochMillis absolute epoch-millis prayer boundary to schedule.
     * @return `true` when AlarmManager accepted the request, or `false` after a permission race.
     */
    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(targetEpochMillis: Long): Boolean {
        return try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                targetEpochMillis,
                boundaryPendingIntent(),
            )
            true
        } catch (exception: SecurityException) {
            false
        }
    }

    /** Cancels the stable PendingIntent used by the prayer-widget boundary alarm. */
    override fun cancel() {
        val pendingIntent = boundaryPendingIntent()
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    /**
     * Creates the immutable explicit broadcast identity shared by scheduling and cancellation.
     *
     * @return stable PendingIntent targeting [PrayerWidgetBoundaryReceiver].
     */
    private fun boundaryPendingIntent(): PendingIntent {
        val intent = Intent(context, PrayerWidgetBoundaryReceiver::class.java).apply {
            action = PrayerWidgetBoundaryReceiver.ACTION_PRAYER_WIDGET_BOUNDARY
        }
        return PendingIntent.getBroadcast(
            context,
            BOUNDARY_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private companion object {
        const val BOUNDARY_REQUEST_CODE = 4_104
    }
}
