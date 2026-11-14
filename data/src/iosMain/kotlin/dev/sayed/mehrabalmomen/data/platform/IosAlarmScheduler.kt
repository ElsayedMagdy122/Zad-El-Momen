package dev.sayed.mehrabalmomen.data.platform

import dev.sayed.mehrabalmomen.domain.model.AlarmTask
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitHour
import platform.Foundation.NSCalendarUnitMinute
import platform.Foundation.NSCalendarUnitSecond
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.Foundation.timeIntervalSince1970
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNNotificationTrigger
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

class IosAlarmScheduler : AlarmScheduler {

    init {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(
            options = UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge,
            completionHandler = { _, _ -> }
        )
    }

    override fun schedule(requestCode: Int, triggerAtMillis: Long, task: AlarmTask) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val identifier = requestCode.toString()

        center.removePendingNotificationRequestsWithIdentifiers(listOf(identifier))

        val title = when (task) {
            is AlarmTask.Prayer -> "الصلاة: ${task.prayerName}"
            is AlarmTask.Reminder -> task.title
            is AlarmTask.DailyRefresh -> "زاد المؤمن"
        }

        val body = when (task) {
            is AlarmTask.Prayer -> "حان الآن موعد صلاة ${task.prayerName}"
            is AlarmTask.Reminder -> task.body
            is AlarmTask.DailyRefresh -> "تذكير يومي"
        }

        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
            setSound(UNNotificationSound.defaultSound)
        }

        val nowMillis = NSDate().timeIntervalSince1970 * 1000.0
        val intervalSeconds = (triggerAtMillis - nowMillis) / 1000.0
        if (intervalSeconds <= 0) return

        val trigger: UNNotificationTrigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            intervalSeconds,
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(identifier, content, trigger)
        center.addNotificationRequest(request) { error ->
            if (error != null) {
                println("Error scheduling iOS notification: ${error.localizedDescription}")
            }
        }
    }

    override fun cancel(requestCode: Int, task: AlarmTask) {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        val identifier = requestCode.toString()
        center.removePendingNotificationRequestsWithIdentifiers(listOf(identifier))
    }

    override fun hasPermission(): Boolean = true
}
