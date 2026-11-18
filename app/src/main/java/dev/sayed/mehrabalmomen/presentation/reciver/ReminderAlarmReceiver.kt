package dev.sayed.mehrabalmomen.presentation.reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dev.sayed.mehrabalmomen.domain.mapper.toNotification
import dev.sayed.mehrabalmomen.domain.model.ReminderNotification
import dev.sayed.mehrabalmomen.domain.model.ReminderType
import dev.sayed.mehrabalmomen.domain.repository.notification.NotificationScheduler
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSchedulerRepository
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ReminderAlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val notificationScheduler: NotificationScheduler by inject()
    private val settingsRepository: ReminderSettingsRepository by inject()
    private val schedulerRepository: ReminderSchedulerRepository by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val reminderId = intent.getLongExtra("REMINDER_ID", -1L)
        val title = intent.getStringExtra("REMINDER_TITLE")
        val body = intent.getStringExtra("REMINDER_BODY") ?: ""
        val typeName = intent.getStringExtra("REMINDER_TYPE_EXTRA")

        val reminderType = runCatching { ReminderType.valueOf(typeName ?: "") }.getOrNull()

        val finalTitle = when {
            !title.isNullOrBlank() -> title
            reminderType != null -> reminderType.toNotification().title
            else -> "تنبيه"
        }

        val finalBody = when {
            body.isNotBlank() -> body
            reminderType != null -> reminderType.toNotification().body
            else -> ""
        }

        val notificationId = if (reminderId != -1L) (1000L + reminderId).toInt() else 100
        val content = ReminderNotification(
            id = notificationId,
            title = finalTitle,
            body = finalBody
        )
        notificationScheduler.showReminder(content)

        if (reminderId != -1L) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    settingsRepository.onReminderTriggered(reminderId)
                    schedulerRepository.rescheduleAll()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
