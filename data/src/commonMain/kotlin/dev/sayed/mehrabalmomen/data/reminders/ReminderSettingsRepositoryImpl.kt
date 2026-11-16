package dev.sayed.mehrabalmomen.data.reminders

import dev.sayed.mehrabalmomen.data.reminders.local.dao.ReminderDao
import dev.sayed.mehrabalmomen.data.reminders.local.entity.ReminderEntity
import dev.sayed.mehrabalmomen.domain.model.RecurrencePattern
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import dev.sayed.mehrabalmomen.domain.model.ReminderType
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderSettingsRepositoryImpl(
    private val reminderDao: ReminderDao
) : ReminderSettingsRepository {

    override fun observeAllReminders(): Flow<List<ReminderConfig>> {
        return reminderDao.getAllReminders()
            .map { list ->
                list.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun saveReminder(reminder: ReminderConfig): Long {
        val entity = ReminderEntity(
            id = reminder.id,
            title = reminder.title,
            description = reminder.description,
            hour = reminder.hour,
            minute = reminder.minute,
            recurrence = reminder.recurrence.name,
            isEnabled = reminder.isEnabled,
            reminderType = reminder.type?.name
        )
        return reminderDao.insert(entity)
    }

    override suspend fun updateReminderEnabled(id: Long, isEnabled: Boolean) {
        reminderDao.updateEnabled(id, isEnabled)
    }

    override suspend fun updateReminderTime(id: Long, hour: Int, minute: Int) {
        reminderDao.updateTime(id, hour, minute)
    }

    override suspend fun updateReminderRecurrence(id: Long, recurrence: RecurrencePattern) {
        reminderDao.updateRecurrence(id, recurrence.name)
    }

    override suspend fun deleteReminder(id: Long) {
        reminderDao.delete(id)
    }

    override suspend fun onReminderTriggered(id: Long) {
        val reminder = reminderDao.getReminderById(id) ?: return
        val pattern = runCatching { RecurrencePattern.valueOf(reminder.recurrence) }.getOrDefault(RecurrencePattern.DAILY)
        if (pattern == RecurrencePattern.ONCE) {
            reminderDao.updateEnabled(id, false)
        }
    }

    private fun ReminderEntity.toDomain(): ReminderConfig {
        val reminderTypeEnum = reminderType?.let { runCatching { ReminderType.valueOf(it) }.getOrNull() }
        val recurrenceEnum = runCatching { RecurrencePattern.valueOf(recurrence) }.getOrDefault(RecurrencePattern.DAILY)
        return ReminderConfig(
            id = id,
            title = title,
            description = description,
            hour = hour,
            minute = minute,
            recurrence = recurrenceEnum,
            isEnabled = isEnabled,
            type = reminderTypeEnum
        )
    }
}
