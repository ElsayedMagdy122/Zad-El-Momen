package dev.sayed.mehrabalmomen.data.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

class AlarmScheduler(
    private val context: Context
) {

    private val alarmManager =
        context.getSystemService(AlarmManager::class.java)

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleExact(
        requestCode: Int,
        triggerAtMillis: Long,
        intent: Intent
    ) {
        if (triggerAtMillis <= System.currentTimeMillis()) return

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(triggerAtMillis, pendingIntent),
            pendingIntent
        )
    }

    fun cancel(requestCode: Int, intent: Intent) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}