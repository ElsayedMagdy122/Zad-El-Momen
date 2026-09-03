package dev.sayed.mehrabalmomen.domain.repository.notification

import dev.sayed.mehrabalmomen.domain.model.ReminderNotification

interface NotificationScheduler {
    fun showReminder(notification: ReminderNotification)
    fun hasPermission(): Boolean
}
