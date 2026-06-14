package dev.sayed.mehrabalmomen.data.reminders

import android.content.Context
import android.content.Intent
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSchedulerRepository
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSettingsRepository
import dev.sayed.mehrabalmomen.presentation.reciver.ReminderAlarmReceiver
import dev.sayed.mehrabalmomen.domain.repository.reminders.TriggerStrategyFactory
import dev.sayed.mehrabalmomen.presentation.utils.AlarmScheduler
import kotlinx.coroutines.flow.first

class ReminderSchedulerRepositoryImpl(
    private val context: Context,
    private val settingsRepository: ReminderSettingsRepository,
    private val alarmScheduler: AlarmScheduler
) : ReminderSchedulerRepository {

    override suspend fun rescheduleAll() {
        val reminders = settingsRepository.observeAllReminders().first()
        reminders.forEach { reminder ->
            val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
                putExtra(REMINDER_TYPE_EXTRA, reminder.type.name)
            }
            alarmScheduler.cancel(reminder.type.alarmId, intent)

            if (reminder.isEnabled) {
                val strategy = TriggerStrategyFactory.getStrategy(reminder.type.recurrence)
                val triggerMillis = strategy.calculateNextTrigger(reminder.hour, reminder.minute)
                alarmScheduler.scheduleExact(reminder.type.alarmId, triggerMillis, intent)
            }
        }
    }

    companion object {
        const val REMINDER_TYPE_EXTRA = "REMINDER_TYPE_EXTRA"
    }
}