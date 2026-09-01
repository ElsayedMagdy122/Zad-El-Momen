package dev.sayed.mehrabalmomen.presentation.reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.sayed.mehrabalmomen.domain.mapper.toNotification
import dev.sayed.mehrabalmomen.domain.model.ReminderType
import dev.sayed.mehrabalmomen.domain.repository.notification.NotificationScheduler
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReminderAlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationScheduler: NotificationScheduler by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val typeName = intent.getStringExtra("REMINDER_TYPE_EXTRA") ?: return
        val reminderType = runCatching { ReminderType.valueOf(typeName) }.getOrNull() ?: return

        val content = reminderType.toNotification()
        notificationScheduler.showReminder(content)
    }
}
