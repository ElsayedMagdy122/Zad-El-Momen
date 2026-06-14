package dev.sayed.mehrabalmomen.domain.repository.reminders

interface ReminderSchedulerRepository {
    suspend fun rescheduleAll()
}