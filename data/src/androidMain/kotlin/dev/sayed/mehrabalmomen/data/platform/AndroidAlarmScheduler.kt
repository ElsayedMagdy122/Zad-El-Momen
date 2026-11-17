package dev.sayed.mehrabalmomen.data.platform

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

    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @SuppressLint("ScheduleExactAlarm")
    override fun schedule(requestCode: Int, triggerAtMillis: Long, task: AlarmTask) {
        if (triggerAtMillis <= System.currentTimeMillis()) return

        val intent = mapTaskToIntent(task)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val canExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                alarmManager.canScheduleExactAlarms()

        try {
            when {
                canExact && task is AlarmTask.Prayer -> {
                    val showIntent = PendingIntent.getActivity(
                        context,
                        requestCode,
                        Intent().setClassName(context, "dev.sayed.mehrabalmomen.presentation.screen.azan.AzanActivity").apply {
                            addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                            )
                            putExtra("PRAYER_NAME", task.prayerName)
                            putExtra("PRAYER_TIME_MILLIS", task.prayerTimeMillis)
                        },
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    alarmManager.setAlarmClock(
                        AlarmManager.AlarmClockInfo(triggerAtMillis, showIntent),
                        pendingIntent
                    )
                }
                canExact -> {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
                else -> {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            }
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }

    override fun cancel(requestCode: Int, task: AlarmTask) {
        val intent = mapTaskToIntent(task)
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) ?: return
        alarmManager.cancel(pi)
        pi.cancel()
    }

    override fun hasPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return alarmManager.canScheduleExactAlarms()
    }

    private fun mapTaskToIntent(task: AlarmTask): Intent {
        return when (task) {
            is AlarmTask.Prayer -> {
                Intent().setClassName(context, "dev.sayed.mehrabalmomen.presentation.reciver.AzanAlarmReceiver").apply {
                    putExtra("PRAYER_NAME", task.prayerName)
                    putExtra("PRAYER_TIME_MILLIS", task.prayerTimeMillis)
                }
            }
            is AlarmTask.DailyRefresh -> {
                Intent().setClassName(context, "dev.sayed.mehrabalmomen.presentation.reciver.DailyRefreshReceiver")
            }
            is AlarmTask.Reminder -> {
                Intent().setClassName(context, "dev.sayed.mehrabalmomen.presentation.reciver.ReminderAlarmReceiver").apply {
                    putExtra("REMINDER_ID", task.reminderId)
                    putExtra("REMINDER_TITLE", task.title)
                    putExtra("REMINDER_BODY", task.body)
                    putExtra("REMINDER_TYPE_EXTRA", task.typeName)
                }
            }
        }
    }
}
