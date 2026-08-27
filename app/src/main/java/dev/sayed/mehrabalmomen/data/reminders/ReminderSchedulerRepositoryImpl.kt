package dev.sayed.mehrabalmomen.data.reminders

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import dev.sayed.mehrabalmomen.domain.model.RescheduleResult
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

    override suspend fun rescheduleAll(): RescheduleResult {
        if (!hasExactAlarmPermission()) {
            return RescheduleResult.PermissionRequired
        }
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
        return RescheduleResult.Success
    }

    private fun hasExactAlarmPermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        return context
            .getSystemService(AlarmManager::class.java)
            .canScheduleExactAlarms()
    }

    companion object {
        const val REMINDER_TYPE_EXTRA = "REMINDER_TYPE_EXTRA"
    }
}