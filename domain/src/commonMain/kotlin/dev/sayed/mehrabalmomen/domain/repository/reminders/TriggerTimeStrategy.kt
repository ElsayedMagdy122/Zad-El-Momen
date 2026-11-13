package dev.sayed.mehrabalmomen.domain.repository.reminders

import dev.sayed.mehrabalmomen.domain.model.RecurrencePattern
import kotlin.time.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

interface TriggerTimeStrategy {
    fun calculateNextTrigger(hour: Int, minute: Int): Long
}

class DailyTriggerStrategy : TriggerTimeStrategy {
    override fun calculateNextTrigger(hour: Int, minute: Int): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val now = Clock.System.now()
        val currentDateTime = now.toLocalDateTime(timeZone)

        var targetDate = currentDateTime.date
        var targetDateTime = LocalDateTime(targetDate, LocalTime(hour, minute, 0, 0))
        var targetInstant = targetDateTime.toInstant(timeZone)

        if (targetInstant <= now) {
            targetDate = targetDate.plus(1, DateTimeUnit.DAY)
            targetDateTime = LocalDateTime(targetDate, LocalTime(hour, minute, 0, 0))
            targetInstant = targetDateTime.toInstant(timeZone)
        }
        return targetInstant.toEpochMilliseconds()
    }
}

class WeeklyThursdayTriggerStrategy : TriggerTimeStrategy {
    override fun calculateNextTrigger(hour: Int, minute: Int): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val now = Clock.System.now()
        val currentDateTime = now.toLocalDateTime(timeZone)

        var targetDate = currentDateTime.date
        var targetDateTime = LocalDateTime(targetDate, LocalTime(hour, minute, 0, 0))
        var targetInstant = targetDateTime.toInstant(timeZone)

        while (targetDate.dayOfWeek != DayOfWeek.THURSDAY || targetInstant <= now) {
            targetDate = targetDate.plus(1, DateTimeUnit.DAY)
            targetDateTime = LocalDateTime(targetDate, LocalTime(hour, minute, 0, 0))
            targetInstant = targetDateTime.toInstant(timeZone)
        }
        return targetInstant.toEpochMilliseconds()
    }
}

class WeeklyTriggerStrategy : TriggerTimeStrategy {
    override fun calculateNextTrigger(hour: Int, minute: Int): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val now = Clock.System.now()
        val currentDateTime = now.toLocalDateTime(timeZone)

        var targetDate = currentDateTime.date
        var targetDateTime = LocalDateTime(targetDate, LocalTime(hour, minute, 0, 0))
        var targetInstant = targetDateTime.toInstant(timeZone)

        if (targetInstant <= now) {
            targetDate = targetDate.plus(7, DateTimeUnit.DAY)
            targetDateTime = LocalDateTime(targetDate, LocalTime(hour, minute, 0, 0))
            targetInstant = targetDateTime.toInstant(timeZone)
        }
        return targetInstant.toEpochMilliseconds()
    }
}

class OnceTriggerStrategy : TriggerTimeStrategy {
    override fun calculateNextTrigger(hour: Int, minute: Int): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val now = Clock.System.now()
        val currentDateTime = now.toLocalDateTime(timeZone)

        var targetDate = currentDateTime.date
        var targetDateTime = LocalDateTime(targetDate, LocalTime(hour, minute, 0, 0))
        var targetInstant = targetDateTime.toInstant(timeZone)

        if (targetInstant <= now) {
            targetDate = targetDate.plus(1, DateTimeUnit.DAY)
            targetDateTime = LocalDateTime(targetDate, LocalTime(hour, minute, 0, 0))
            targetInstant = targetDateTime.toInstant(timeZone)
        }
        return targetInstant.toEpochMilliseconds()
    }
}

object TriggerStrategyFactory {
    fun getStrategy(pattern: RecurrencePattern): TriggerTimeStrategy = when (pattern) {
        RecurrencePattern.DAILY -> DailyTriggerStrategy()
        RecurrencePattern.WEEKLY_THURSDAY -> WeeklyThursdayTriggerStrategy()
        RecurrencePattern.WEEKLY -> WeeklyTriggerStrategy()
        RecurrencePattern.ONCE -> OnceTriggerStrategy()
    }
}
