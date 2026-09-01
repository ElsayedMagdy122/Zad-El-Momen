package dev.sayed.mehrabalmomen.data.prayer.repository

import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerAlarm
import dev.sayed.mehrabalmomen.domain.model.AlarmTask
import dev.sayed.mehrabalmomen.domain.model.RescheduleResult
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerAlarmRepository
import java.util.Calendar

class PrayerAlarmRepositoryImpl(
    private val alarmScheduler: AlarmScheduler
) : PrayerAlarmRepository {

    override fun reschedule(prayers: List<PrayerAlarm>): RescheduleResult {

        if (!alarmScheduler.hasPermission()) {
            return RescheduleResult.PermissionRequired
        }

        scheduleEnabledPrayerAlarms(prayers)
        scheduleMidnight()
        return RescheduleResult.Success
    }

    private fun scheduleEnabledPrayerAlarms(prayers: List<PrayerAlarm>) {
        prayers.forEach { prayer ->
            alarmScheduler.cancel(prayer.id, AlarmTask.Prayer(prayer.name.name))
        }

        prayers.filter { it.enabled }.forEach { prayer ->
            alarmScheduler.schedule(prayer.id, prayer.timeMillis, AlarmTask.Prayer(prayer.name.name))
        }
    }

    private fun scheduleMidnight() {
        alarmScheduler.schedule(
            MIDNIGHT_ROLLOVER_REQUEST_CODE,
            calculateNextMidnightMillis(),
            AlarmTask.DailyRefresh
        )
    }

    private fun calculateNextMidnightMillis(): Long =
        Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 1)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private companion object {
        const val MIDNIGHT_ROLLOVER_REQUEST_CODE = 99
    }
}