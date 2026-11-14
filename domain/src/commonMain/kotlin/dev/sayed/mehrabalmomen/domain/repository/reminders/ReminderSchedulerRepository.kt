package dev.sayed.mehrabalmomen.domain.repository.reminders

import dev.sayed.mehrabalmomen.domain.model.RescheduleResult

interface ReminderSchedulerRepository {
    suspend fun rescheduleAll(): RescheduleResult
}
