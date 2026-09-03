package dev.sayed.mehrabalmomen.domain.repository.reminders

import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import dev.sayed.mehrabalmomen.domain.model.ReminderType
import kotlinx.coroutines.flow.Flow

interface ReminderSettingsRepository {
    fun observeAllReminders(): Flow<List<ReminderConfig>>
    suspend fun saveReminderConfig(type: ReminderType, isEnabled: Boolean, hour: Int, minute: Int)
}
