package dev.sayed.mehrabalmomen.data.platform

import dev.sayed.mehrabalmomen.domain.model.AlarmTask
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNTimeIntervalNotificationTrigger
import platform.UserNotifications.UNUserNotificationCenter

class IosAlarmScheduler(
    private val settingsRepository: SettingsRepository
) : AlarmScheduler {

    init {
        requestNotificationPermission()
    }

    private fun requestNotificationPermission() {
        val center = UNUserNotificationCenter.currentNotificationCenter()
        center.requestAuthorizationWithOptions(
            UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
        ) { _, _ -> }
    }

    override fun schedule(requestCode: Int, triggerAtMillis: Long, task: AlarmTask) {
        val currentTime = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val delayMillis = triggerAtMillis - currentTime
        println("IosAlarmScheduler: schedule called for requestCode=$requestCode, task=$task, triggerAt=$triggerAtMillis, currentTime=$currentTime, delayMillis=$delayMillis")

        if (delayMillis <= 0L) {
            println("IosAlarmScheduler: [IGNORED] Reminder trigger time is in the past or now (delayMillis=$delayMillis)")
            return
        }

        val (title, body) = when (task) {
            is AlarmTask.Prayer -> "حَيَّ عَلَى الصَّلاةِ" to "حان الآن موعد أذان ${getPrayerDisplayName(task.prayerName)}"
            is AlarmTask.Reminder -> (task.title.ifBlank { "تنبيه" }) to task.body
            is AlarmTask.DailyRefresh -> {
                println("IosAlarmScheduler: [IGNORED] DailyRefresh task")
                return
            }
        }

        val sound = UNNotificationSound.defaultSound()

        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
            setSound(sound)
        }

        val intervalSeconds = (delayMillis.toDouble() / 1000.0).coerceAtLeast(1.0)
        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = intervalSeconds,
            repeats = false
        )

        val identifier = "reminder_alarm_$requestCode"
        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = identifier,
            content = content,
            trigger = trigger
        )

        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request) { error ->
            if (error != null) {
                println("IosAlarmScheduler: [ERROR] Failed to schedule notification '$identifier': ${error.localizedDescription}")
            } else {
                println("IosAlarmScheduler: [SUCCESS] Successfully scheduled notification '$identifier' to fire in $intervalSeconds seconds (Title: $title)")
            }
        }

        UNUserNotificationCenter.currentNotificationCenter().getPendingNotificationRequestsWithCompletionHandler { requests ->
            println("IosAlarmScheduler: Currently pending notifications count in system = ${requests?.size ?: 0}")
        }
    }

    override fun cancel(requestCode: Int, task: AlarmTask) {
        val identifier = "reminder_alarm_$requestCode"
        println("IosAlarmScheduler: cancel called for identifier=$identifier")
        UNUserNotificationCenter.currentNotificationCenter()
            .removePendingNotificationRequestsWithIdentifiers(listOf(identifier))
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
