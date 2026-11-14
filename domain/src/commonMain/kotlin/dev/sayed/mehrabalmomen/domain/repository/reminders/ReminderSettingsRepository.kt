package dev.sayed.mehrabalmomen.domain.repository.reminders

import dev.sayed.mehrabalmomen.domain.model.RecurrencePattern
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import kotlinx.coroutines.flow.Flow

interface ReminderSettingsRepository {
    fun observeAllReminders(): Flow<List<ReminderConfig>>
    suspend fun saveReminder(reminder: ReminderConfig): Long
    suspend fun updateReminderEnabled(id: Long, isEnabled: Boolean)
    suspend fun updateReminderTime(id: Long, hour: Int, minute: Int)
    suspend fun updateReminderRecurrence(id: Long, recurrence: RecurrencePattern)
    suspend fun deleteReminder(id: Long)
    suspend fun onReminderTriggered(id: Long)
}
