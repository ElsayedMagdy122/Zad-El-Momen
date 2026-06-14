package dev.sayed.mehrabalmomen.presentation.reciver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.data.reminders.ReminderSchedulerRepositoryImpl
import dev.sayed.mehrabalmomen.domain.model.ReminderType


class ReminderAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val typeName = intent.getStringExtra(ReminderSchedulerRepositoryImpl.REMINDER_TYPE_EXTRA) ?: return
        val reminderType = runCatching { ReminderType.valueOf(typeName) }.getOrNull() ?: return

        showNotification(context, reminderType)
    }

    private fun showNotification(
        context: Context,
        type: ReminderType
    ) {

        createNotificationChannel(context)

        val launchIntent =
            context.packageManager.getLaunchIntentForPackage(context.packageName)

        val pendingIntent =
            PendingIntent.getActivity(
                context,
                type.alarmId,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

        val notification =
            NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_mosque_02)
                .setContentTitle(getTitle(type))
                .setContentText(getBody(type))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()

        NotificationManagerCompat
            .from(context)
            .notify(type.alarmId, notification)
    }

    private fun createNotificationChannel(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "الأذكار والتنبيهات",
                    NotificationManager.IMPORTANCE_HIGH
                )

            val manager =
                context.getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }

    private fun getTitle(type: ReminderType): String {
        return when (type) {
            ReminderType.MORNING_AZKAR ->
                "أذكار الصباح"

            ReminderType.EVENING_AZKAR ->
                "أذكار المساء"

            ReminderType.FRIDAY_SUNNAN ->
                "سنن الجمعة"

            ReminderType.DAILY_WORD ->
                "الورد اليومي"
        }
    }

    private fun getBody(type: ReminderType): String {
        return when (type) {

            ReminderType.MORNING_AZKAR ->
                "لا تنس أذكار الصباح"

            ReminderType.EVENING_AZKAR ->
                "لا تنس أذكار المساء"

            ReminderType.FRIDAY_SUNNAN ->
                "أكثر من الصلاة على النبي واقرأ سورة الكهف"

            ReminderType.DAILY_WORD ->
                "حان وقت وردك القرآني اليومي"
        }
    }

    companion object {
        const val CHANNEL_ID = "reminder_channel"
    }
}