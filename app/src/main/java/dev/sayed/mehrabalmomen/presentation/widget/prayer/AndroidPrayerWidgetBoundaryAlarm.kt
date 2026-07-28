package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/**
 * AlarmManager implementation of the prayer-widget exact alarms.
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
    override fun schedulePrayerBoundary(targetEpochMillis: Long): Boolean {
        return schedule(
            action = PrayerWidgetBoundaryReceiver.ACTION_PRAYER_WIDGET_BOUNDARY,
            requestCode = BOUNDARY_REQUEST_CODE,
            targetEpochMillis = targetEpochMillis,
        )
    }

    /**
     * Schedules an exact idle-capable broadcast at the next local midnight.
     *
     * The local-midnight alarm uses a separate PendingIntent identity so it cannot replace the
     * prayer-boundary alarm. A permission race is converted into `false`.
     *
     * @param targetEpochMillis absolute epoch-millis local midnight instant to schedule.
     * @return `true` when AlarmManager accepted the request, or `false` after a permission race.
     */
    @SuppressLint("ScheduleExactAlarm")
    override fun scheduleLocalMidnight(targetEpochMillis: Long): Boolean {
        return schedule(
            action = PrayerWidgetBoundaryReceiver.ACTION_PRAYER_WIDGET_LOCAL_MIDNIGHT,
            requestCode = LOCAL_MIDNIGHT_REQUEST_CODE,
            targetEpochMillis = targetEpochMillis,
        )
    }

    /**
     * Cancels both widget-only exact alarms without affecting Azan notification alarms.
     *
     * @return no value; after completion both stable widget PendingIntents are removed.
     */
    override fun cancelAll() {
        cancel(
            action = PrayerWidgetBoundaryReceiver.ACTION_PRAYER_WIDGET_BOUNDARY,
            requestCode = BOUNDARY_REQUEST_CODE,
        )
        cancel(
            action = PrayerWidgetBoundaryReceiver.ACTION_PRAYER_WIDGET_LOCAL_MIDNIGHT,
            requestCode = LOCAL_MIDNIGHT_REQUEST_CODE,
        )
    }

    /**
     * Schedules one exact widget broadcast using the supplied PendingIntent identity.
     *
     * @param action explicit broadcast action handled by [PrayerWidgetBoundaryReceiver].
     * @param requestCode stable request code that distinguishes widget alarm types.
     * @param targetEpochMillis absolute epoch-millis instant to schedule.
     * @return `true` when AlarmManager accepted the request, or `false` after a permission race.
     */
    @SuppressLint("ScheduleExactAlarm")
    private fun schedule(
        action: String,
        requestCode: Int,
        targetEpochMillis: Long,
    ): Boolean {
        return try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                targetEpochMillis,
                pendingIntent(action, requestCode),
            )
            true
        } catch (exception: SecurityException) {
            false
        }
    }

    /**
     * Cancels one exact widget broadcast by recreating its stable PendingIntent identity.
     *
     * @param action explicit broadcast action used when the alarm was scheduled.
     * @param requestCode stable request code used when the alarm was scheduled.
     * @return no value; after completion the matching alarm identity is cancelled if present.
     */
    private fun cancel(action: String, requestCode: Int) {
        val pendingIntent = pendingIntent(action, requestCode)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    /**
     * Creates the immutable explicit broadcast identity shared by scheduling and cancellation.
     *
     * @param action explicit broadcast action that describes the widget refresh trigger.
     * @param requestCode stable request code that distinguishes widget alarm types.
     * @return stable PendingIntent targeting [PrayerWidgetBoundaryReceiver].
     */
    private fun pendingIntent(action: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, PrayerWidgetBoundaryReceiver::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private companion object {
        const val BOUNDARY_REQUEST_CODE = 4_104
        const val LOCAL_MIDNIGHT_REQUEST_CODE = 4_105
    }
}
