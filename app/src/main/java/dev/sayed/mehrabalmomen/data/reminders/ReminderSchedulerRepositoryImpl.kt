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
            val requestCode = (1000L + reminder.id).toInt()
            val task = AlarmTask.Reminder(
                reminderId = reminder.id,
                title = reminder.title,
                body = reminder.description,
                typeName = reminder.type?.name
            )
            alarmScheduler.cancel(requestCode, task)

            if (reminder.isEnabled) {
                val strategy = TriggerStrategyFactory.getStrategy(reminder.recurrence)
                val triggerMillis = strategy.calculateNextTrigger(reminder.hour, reminder.minute)
                alarmScheduler.schedule(requestCode, triggerMillis, task)
            }
        }
        return RescheduleResult.Success
    }

    companion object {
        const val REMINDER_TYPE_EXTRA = "REMINDER_TYPE_EXTRA"
    }
}
