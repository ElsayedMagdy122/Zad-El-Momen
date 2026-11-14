package dev.sayed.mehrabalmomen.domain.repository.reminders

import dev.sayed.mehrabalmomen.domain.model.RecurrencePattern
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
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
        val nowInstant = Clock.System.now()
        val nowLocal = nowInstant.toLocalDateTime(timeZone)

        var targetLocal = LocalDateTime(nowLocal.year, nowLocal.monthNumber, nowLocal.dayOfMonth, hour, minute, 0, 0)
        var targetInstant = targetLocal.toInstant(timeZone)

        if (targetInstant <= nowInstant) {
            val nextDay = nowLocal.date.plus(1, DateTimeUnit.DAY)
            targetLocal = LocalDateTime(nextDay.year, nextDay.monthNumber, nextDay.dayOfMonth, hour, minute, 0, 0)
            targetInstant = targetLocal.toInstant(timeZone)
        }
        return targetInstant.toEpochMilliseconds()
    }
}

class WeeklyThursdayTriggerStrategy : TriggerTimeStrategy {
    override fun calculateNextTrigger(hour: Int, minute: Int): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val nowInstant = Clock.System.now()
        val nowLocal = nowInstant.toLocalDateTime(timeZone)

        var currentDate = nowLocal.date
        var targetLocal = LocalDateTime(currentDate.year, currentDate.monthNumber, currentDate.dayOfMonth, hour, minute, 0, 0)
        var targetInstant = targetLocal.toInstant(timeZone)

        while (currentDate.dayOfWeek != DayOfWeek.THURSDAY || targetInstant <= nowInstant) {
            currentDate = currentDate.plus(1, DateTimeUnit.DAY)
            targetLocal = LocalDateTime(currentDate.year, currentDate.monthNumber, currentDate.dayOfMonth, hour, minute, 0, 0)
            targetInstant = targetLocal.toInstant(timeZone)
        }
        return targetInstant.toEpochMilliseconds()
    }
}

class WeeklyTriggerStrategy : TriggerTimeStrategy {
    override fun calculateNextTrigger(hour: Int, minute: Int): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val nowInstant = Clock.System.now()
        val nowLocal = nowInstant.toLocalDateTime(timeZone)

        var targetLocal = LocalDateTime(nowLocal.year, nowLocal.monthNumber, nowLocal.dayOfMonth, hour, minute, 0, 0)
        var targetInstant = targetLocal.toInstant(timeZone)

        if (targetInstant <= nowInstant) {
            val nextWeek = nowLocal.date.plus(7, DateTimeUnit.DAY)
            targetLocal = LocalDateTime(nextWeek.year, nextWeek.monthNumber, nextWeek.dayOfMonth, hour, minute, 0, 0)
            targetInstant = targetLocal.toInstant(timeZone)
        }
        return targetInstant.toEpochMilliseconds()
    }
}

class OnceTriggerStrategy : TriggerTimeStrategy {
    override fun calculateNextTrigger(hour: Int, minute: Int): Long {
        val timeZone = TimeZone.currentSystemDefault()
        val nowInstant = Clock.System.now()
        val nowLocal = nowInstant.toLocalDateTime(timeZone)

        var targetLocal = LocalDateTime(nowLocal.year, nowLocal.monthNumber, nowLocal.dayOfMonth, hour, minute, 0, 0)
        var targetInstant = targetLocal.toInstant(timeZone)

        if (targetInstant <= nowInstant) {
            val nextDay = nowLocal.date.plus(1, DateTimeUnit.DAY)
            targetLocal = LocalDateTime(nextDay.year, nextDay.monthNumber, nextDay.dayOfMonth, hour, minute, 0, 0)
            targetInstant = targetLocal.toInstant(timeZone)
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
