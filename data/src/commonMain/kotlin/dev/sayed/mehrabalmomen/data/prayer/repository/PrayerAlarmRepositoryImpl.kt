package dev.sayed.mehrabalmomen.data.prayer.repository

import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerAlarm
import dev.sayed.mehrabalmomen.domain.model.AlarmTask
import dev.sayed.mehrabalmomen.domain.model.RescheduleResult
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerAlarmRepository
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

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

    fun hasExactAlarmPermission(): Boolean {
        return alarmScheduler.hasPermission()
    }

    private fun scheduleEnabledPrayerAlarms(prayers: List<PrayerAlarm>) {
        prayers.filter { it.enabled }.forEach { prayer ->
            alarmScheduler.schedule(prayer.id, prayer.timeMillis, AlarmTask.Prayer(prayer.name.name, prayer.timeMillis))
        }
    }

    private fun scheduleMidnight() {
        alarmScheduler.schedule(
            MIDNIGHT_ROLLOVER_REQUEST_CODE,
            calculateNextMidnightMillis(),
            AlarmTask.DailyRefresh
        )
    }

    private fun calculateNextMidnightMillis(): Long {
        val now = Clock.System.now()
        val tz = TimeZone.currentSystemDefault()
        val tomorrow = now.plus(1, DateTimeUnit.DAY, tz).toLocalDateTime(tz).date
        val midnight = LocalDateTime(tomorrow.year, tomorrow.month, tomorrow.day, 0, 1, 0, 0)
        return midnight.toInstant(tz).toEpochMilliseconds()
    }

    private companion object {
        const val MIDNIGHT_ROLLOVER_REQUEST_CODE = 99
    }
}
