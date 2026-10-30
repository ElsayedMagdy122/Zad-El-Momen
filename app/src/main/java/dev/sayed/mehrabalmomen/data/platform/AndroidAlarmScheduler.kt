package dev.sayed.mehrabalmomen.data.platform

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dev.sayed.mehrabalmomen.domain.model.AlarmTask
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import dev.sayed.mehrabalmomen.presentation.reciver.AzanAlarmReceiver
import dev.sayed.mehrabalmomen.presentation.reciver.DailyRefreshReceiver
import dev.sayed.mehrabalmomen.presentation.reciver.ReminderAlarmReceiver
import dev.sayed.mehrabalmomen.presentation.screen.azan.AzanActivity
import dev.sayed.mehrabalmomen.presentation.utils.Constants.PRAYER_NAME_KEY
import dev.sayed.mehrabalmomen.presentation.utils.Constants.PRAYER_TIME_MILLIS_KEY

/**
 * Android implementation of [AlarmScheduler] using [AlarmManager].
 */
class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(requestCode: Int, triggerAtMillis: Long, task: AlarmTask) {
        // Avoid scheduling alarms in the past
        if (triggerAtMillis <= System.currentTimeMillis()) return

        // Safety check for exact alarm permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) return
        }

        val intent = mapTaskToIntent(task)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = if (task is AlarmTask.Prayer) {
            val activityIntent = Intent(context, AzanActivity::class.java).apply {
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
                putExtra(PRAYER_NAME_KEY, task.prayerName)
                putExtra(PRAYER_TIME_MILLIS_KEY, task.prayerTimeMillis)
            }
            PendingIntent.getActivity(
                context,
                requestCode,
                activityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else {
            pendingIntent
        }

        try {
            // Prefer setAlarmClock for maximum precision as it's highly visible to the system
            alarmManager.setAlarmClock(
                AlarmManager.AlarmClockInfo(triggerAtMillis, showIntent),
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Fallback for cases where exact alarm permission is revoked at runtime
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    override fun cancel(requestCode: Int, task: AlarmTask) {
        val intent = mapTaskToIntent(task)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    override fun hasPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return alarmManager.canScheduleExactAlarms()
    }

    /**
     * Maps domain [AlarmTask] definitions to platform-specific [Intent]s.
     */
    private fun mapTaskToIntent(task: AlarmTask): Intent {
        return when (task) {
            is AlarmTask.Prayer -> {
                Intent(context, AzanAlarmReceiver::class.java).apply {
                    putExtra(PRAYER_NAME_KEY, task.prayerName)
                    putExtra(PRAYER_TIME_MILLIS_KEY, task.prayerTimeMillis)
                }
            }
            is AlarmTask.DailyRefresh -> {
                Intent(context, DailyRefreshReceiver::class.java)
            }
            is AlarmTask.Reminder -> {
                Intent(context, ReminderAlarmReceiver::class.java).apply {
                    putExtra("REMINDER_TYPE_EXTRA", task.typeName)
                }
            }
        }
    }
}
