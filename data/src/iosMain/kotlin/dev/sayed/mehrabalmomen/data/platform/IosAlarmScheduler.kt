package dev.sayed.mehrabalmomen.data.platform

import dev.sayed.mehrabalmomen.domain.model.AlarmTask
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter

class IosAlarmScheduler : AlarmScheduler {
    override fun schedule(requestCode: Int, triggerAtMillis: Long, task: AlarmTask) {
        val currentTime = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val delayMillis = triggerAtMillis - currentTime
        if (delayMillis <= 0L) return

        val (title, body) = when (task) {
            is AlarmTask.Prayer -> "حَيَّ عَلَى الصَّلاةِ" to "حان الآن موعد أذان ${getPrayerDisplayName(task.prayerName)}"
            is AlarmTask.Reminder -> (task.title.ifBlank { "تنبيه" }) to task.body
            is AlarmTask.DailyRefresh -> return
        }

        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
            setSound(UNNotificationSound.defaultSound())
        }

        val trigger = platform.UserNotifications.UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = (delayMillis.toDouble() / 1000.0).coerceAtLeast(1.0),
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "prayer_alarm_$requestCode",
            content = content,
            trigger = trigger
        )

        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request) { _ -> }
    }

    override fun cancel(requestCode: Int, task: AlarmTask) {
        UNUserNotificationCenter.currentNotificationCenter()
            .removePendingNotificationRequestsWithIdentifiers(listOf("prayer_alarm_$requestCode"))
    }

    override fun hasPermission(): Boolean {
        return true
    }

    private fun getPrayerDisplayName(name: String): String = when (name.uppercase()) {
        "FAJR" -> "الفجر"
        "ZUHR", "DHUHR" -> "الظهر"
        "ASR" -> "العصر"
        "MAGHRIB" -> "المغرب"
        "ISHA" -> "العشاء"
        else -> name
    }
}
