package dev.sayed.mehrabalmomen.domain.repository.reminders

import dev.sayed.mehrabalmomen.domain.model.RecurrencePattern
import java.util.Calendar

interface TriggerTimeStrategy {
    fun calculateNextTrigger(hour: Int, minute: Int): Long
}

class DailyTriggerStrategy : TriggerTimeStrategy {
    override fun calculateNextTrigger(hour: Int, minute: Int): Long {
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (target.before(Calendar.getInstance())) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis
    }
}

class WeeklyThursdayTriggerStrategy : TriggerTimeStrategy {
    override fun calculateNextTrigger(hour: Int, minute: Int): Long {
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val now = Calendar.getInstance()
        while (target.get(Calendar.DAY_OF_WEEK) != Calendar.THURSDAY || target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis
    }
}

object TriggerStrategyFactory {
    fun getStrategy(pattern: RecurrencePattern): TriggerTimeStrategy = when (pattern) {
        RecurrencePattern.DAILY -> DailyTriggerStrategy()
        RecurrencePattern.WEEKLY_THURSDAY -> WeeklyThursdayTriggerStrategy()
    }
}