package dev.sayed.mehrabalmomen.domain.repository.reminders

import dev.sayed.mehrabalmomen.domain.model.RecurrencePattern
import dev.sayed.mehrabalmomen.domain.utils.getCurrentTimeMillis
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

interface TriggerTimeStrategy {
    fun calculateNextTrigger(hour: Int, minute: Int): Long
}

@OptIn(ExperimentalTime::class)
class DailyTriggerStrategy : TriggerTimeStrategy {
    override fun calculateNextTrigger(hour: Int, minute: Int): Long {
        val now = Instant.fromEpochMilliseconds(getCurrentTimeMillis())
        val timeZone = TimeZone.currentSystemDefault()
        val today = now.toLocalDateTime(timeZone)
        var target = LocalDateTime(today.year, today.month, today.dayOfMonth, hour, minute, 0, 0)
        var targetInstant = target.toInstant(timeZone)
        
        if (targetInstant < now) {
            targetInstant = targetInstant.plus(1, DateTimeUnit.DAY, timeZone)
        }
        return targetInstant.toEpochMilliseconds()
    }
}

@OptIn(ExperimentalTime::class)
class WeeklyThursdayTriggerStrategy : TriggerTimeStrategy {
    override fun calculateNextTrigger(hour: Int, minute: Int): Long {
        val now = Instant.fromEpochMilliseconds(getCurrentTimeMillis())
        val timeZone = TimeZone.currentSystemDefault()
        val today = now.toLocalDateTime(timeZone)
        var target = LocalDateTime(today.year, today.month, today.dayOfMonth, hour, minute, 0, 0)
        var targetInstant = target.toInstant(timeZone)
        
        while (targetInstant.toLocalDateTime(timeZone).dayOfWeek != DayOfWeek.THURSDAY || targetInstant < now) {
            targetInstant = targetInstant.plus(1, DateTimeUnit.DAY, timeZone)
        }
        return targetInstant.toEpochMilliseconds()
    }
}

object TriggerStrategyFactory {
    fun getStrategy(pattern: RecurrencePattern): TriggerTimeStrategy = when (pattern) {
        RecurrencePattern.DAILY -> DailyTriggerStrategy()
        RecurrencePattern.WEEKLY_THURSDAY -> WeeklyThursdayTriggerStrategy()
    }
}
