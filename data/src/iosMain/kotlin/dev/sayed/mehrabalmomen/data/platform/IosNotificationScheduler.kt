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
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val identifier = notification.id.toString()

        val content = UNMutableNotificationContent().apply {
            setTitle(notification.title)
            setBody(notification.body)
            setSound(UNNotificationSound.defaultSound)
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            1.0,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(identifier, content, trigger)
        center.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Error showing iOS notification: ${error.localizedDescription}")
            }
        }
    }

    override fun hasPermission(): Boolean = true
}
