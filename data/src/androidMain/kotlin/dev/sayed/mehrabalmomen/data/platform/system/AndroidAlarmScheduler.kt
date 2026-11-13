package dev.sayed.mehrabalmomen.data.platform.system

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import dev.sayed.mehrabalmomen.domain.model.AlarmTask
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler

/**
 * Android implementation of [AlarmScheduler] using [AlarmManager].
 */
class AndroidAlarmScheduler(
    private val context: Context
) : AlarmScheduler {

    private val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(requestCode: Int, triggerAtMillis: Long, task: AlarmTask) {
        val manager = alarmManager ?: return
        // Avoid scheduling alarms in the past
        if (triggerAtMillis <= System.currentTimeMillis()) return

        // Safety check for exact alarm permission on Android 12+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!manager.canScheduleExactAlarms()) return
        }

        val intent = mapTaskToIntent(task)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val showIntent = if (task is AlarmTask.Prayer) {
            val activityIntent = Intent().apply {
                setClassName(context.packageName, "dev.sayed.mehrabalmomen.presentation.screen.azan.AzanActivity")
                addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_SINGLE_TOP
                )
                putExtra("PRAYER_NAME", task.prayerName)
                putExtra("PRAYER_TIME_MILLIS", task.prayerTimeMillis)
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
            manager.setAlarmClock(
                AlarmManager.AlarmClockInfo(triggerAtMillis, showIntent),
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Fallback for cases where exact alarm permission is revoked at runtime
            manager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    override fun cancel(requestCode: Int, task: AlarmTask) {
        val manager = alarmManager ?: return
        val intent = mapTaskToIntent(task)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        manager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    override fun hasPermission(): Boolean {
        val manager = alarmManager ?: return false
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return manager.canScheduleExactAlarms()
    }

    /**
     * Maps domain [AlarmTask] definitions to platform-specific [Intent]s.
     */
    private fun mapTaskToIntent(task: AlarmTask): Intent {
        return when (task) {
            is AlarmTask.Prayer -> {
                Intent().apply {
                    setClassName(context.packageName, "dev.sayed.mehrabalmomen.presentation.reciver.AzanAlarmReceiver")
                    putExtra("PRAYER_NAME", task.prayerName)
                    putExtra("PRAYER_TIME_MILLIS", task.prayerTimeMillis)
                }
            }
            is AlarmTask.DailyRefresh -> {
                Intent().apply {
                    setClassName(context.packageName, "dev.sayed.mehrabalmomen.presentation.reciver.DailyRefreshReceiver")
                }
            }
            is AlarmTask.Reminder -> {
                Intent().apply {
                    setClassName(context.packageName, "dev.sayed.mehrabalmomen.presentation.reciver.ReminderAlarmReceiver")
                    putExtra("REMINDER_ID", task.reminderId)
                    putExtra("REMINDER_TITLE", task.title)
                    putExtra("REMINDER_BODY", task.body)
                    putExtra("REMINDER_TYPE_EXTRA", task.typeName)
                }
            }
        }
    }
}
