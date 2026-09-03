package dev.sayed.mehrabalmomen.data.reminders

import dev.sayed.mehrabalmomen.domain.model.AlarmTask
import dev.sayed.mehrabalmomen.domain.model.RescheduleResult
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSchedulerRepository
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSettingsRepository
import dev.sayed.mehrabalmomen.domain.repository.reminders.TriggerStrategyFactory
import kotlinx.coroutines.flow.first

class ReminderSchedulerRepositoryImpl(
    private val settingsRepository: ReminderSettingsRepository,
    private val alarmScheduler: AlarmScheduler
) : ReminderSchedulerRepository {

    override suspend fun rescheduleAll(): RescheduleResult {
        if (!alarmScheduler.hasPermission()) {
            return RescheduleResult.PermissionRequired
        }
        val reminders = settingsRepository.observeAllReminders().first()
        reminders.forEach { reminder ->
            val task = AlarmTask.Reminder(reminder.type.name)
            alarmScheduler.cancel(reminder.type.alarmId, task)

            if (reminder.isEnabled) {
                val strategy = TriggerStrategyFactory.getStrategy(reminder.type.recurrence)
                val triggerMillis = strategy.calculateNextTrigger(reminder.hour, reminder.minute)
                alarmScheduler.schedule(reminder.type.alarmId, triggerMillis, task)
            }
        }
        return RescheduleResult.Success
    }

    companion object {
        const val REMINDER_TYPE_EXTRA = "REMINDER_TYPE_EXTRA"
    }
}
