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
        if (delayMillis <= 0L) return

        val (title, body) = when (task) {
            is AlarmTask.Prayer -> "حَيَّ عَلَى الصَّلاةِ" to "حان الآن موعد أذان ${getPrayerDisplayName(task.prayerName)}"
            is AlarmTask.Reminder -> (task.title.ifBlank { "تنبيه" }) to task.body
            is AlarmTask.DailyRefresh -> return
        }

        val moazenFile = runBlocking {
            settingsRepository.observeSelectedMoazen().first()
        }.removeSuffix(".mp3")

        val sound = UNNotificationSound.soundNamed("$moazenFile.mp3") ?: UNNotificationSound.defaultSound()

        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(body)
            setSound(sound)
        }

        val trigger = UNTimeIntervalNotificationTrigger.triggerWithTimeInterval(
            timeInterval = (delayMillis.toDouble() / 1000.0).coerceAtLeast(1.0),
            repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(
            identifier = "reminder_alarm_$requestCode",
            content = content,
            trigger = trigger
        )

        UNUserNotificationCenter.currentNotificationCenter().addNotificationRequest(request) { _ -> }
    }

    override fun cancel(requestCode: Int, task: AlarmTask) {
        UNUserNotificationCenter.currentNotificationCenter()
            .removePendingNotificationRequestsWithIdentifiers(listOf("reminder_alarm_$requestCode"))
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
