package dev.sayed.mehrabalmomen.data.platform

import dev.sayed.mehrabalmomen.domain.model.ReminderNotification
import dev.sayed.mehrabalmomen.domain.repository.notification.NotificationScheduler
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

class IosNotificationScheduler : NotificationScheduler {
    override fun showReminder(notification: ReminderNotification) {
        val content = UNMutableNotificationContent().apply {
            setTitle(notification.title)
            setBody(notification.body)
            setSound(UNNotificationSound.defaultSound())
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = 1.0,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "reminder_${notification.id}",
            content = content,
            trigger = trigger
        )

        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request) { _ -> }
    }

    override fun hasPermission(): Boolean {
        return true
    }
}
