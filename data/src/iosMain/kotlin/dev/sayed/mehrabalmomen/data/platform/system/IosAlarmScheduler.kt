package dev.sayed.mehrabalmomen.data.platform.system

import dev.sayed.mehrabalmomen.domain.model.AlarmTask
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler

class IosAlarmScheduler : AlarmScheduler {
    override fun schedule(requestCode: Int, triggerAtMillis: Long, task: AlarmTask) {
        // iOS alarm / local notification scheduling via UNUserNotificationCenter
    }

    override fun cancel(requestCode: Int, task: AlarmTask) {
        // iOS cancel via UNUserNotificationCenter
    }

    override fun hasPermission(): Boolean = true
}
