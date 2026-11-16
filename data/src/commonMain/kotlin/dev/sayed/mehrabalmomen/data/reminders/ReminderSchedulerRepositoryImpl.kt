package dev.sayed.mehrabalmomen.data.reminders

import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.model.AlarmTask
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import dev.sayed.mehrabalmomen.domain.model.ReminderType
import dev.sayed.mehrabalmomen.domain.model.RescheduleResult
import dev.sayed.mehrabalmomen.domain.repository.prayer.AlarmScheduler
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSchedulerRepository
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSettingsRepository
import dev.sayed.mehrabalmomen.domain.repository.reminders.TriggerStrategyFactory
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ReminderSchedulerRepositoryImpl(
    private val settingsRepository: ReminderSettingsRepository,
    private val alarmScheduler: AlarmScheduler,
    private val prayerRepository: PrayerRepository,
    private val appSettingsRepository: SettingsRepository
) : ReminderSchedulerRepository {

    @OptIn(ExperimentalTime::class)
    override suspend fun rescheduleAll(): RescheduleResult {
        if (!alarmScheduler.hasPermission()) {
            return RescheduleResult.PermissionRequired
        }
        val reminders = settingsRepository.observeAllReminders().first()
        reminders.forEach { reminder ->
            if (reminder.type == ReminderType.BEFORE_PRAYER) {
                scheduleBeforePrayerReminders(reminder)
            } else {
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
        }
        return RescheduleResult.Success
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun scheduleBeforePrayerReminders(reminder: ReminderConfig) {
        Prayer.PrayerName.entries.forEach { prayerName ->
            val requestCode = 3000 + prayerName.ordinal
            alarmScheduler.cancel(
                requestCode,
                AlarmTask.Reminder(reminderId = reminder.id, typeName = ReminderType.BEFORE_PRAYER.name)
            )
        }

        if (!reminder.isEnabled) return

        val appSettings = appSettingsRepository.observeAppSettings().first().prayerSettings
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val location = Location(
            latitude = appSettings.location.latitude,
            longitude = appSettings.location.longitude
        )

        val todayPrayers = runCatching {
            prayerRepository.getDailyPrayers(
                madhab = appSettings.madhab,
                calculationMethod = appSettings.calculationMethod,
                location = location,
                date = today
            )
        }.getOrNull() ?: return

        val durationMinutes = reminder.minute.coerceAtLeast(1)
        val now = System.currentTimeMillis()

        todayPrayers.forEach { prayer ->
            val requestCode = 3000 + prayer.name.ordinal
            val triggerMillis = prayer.time.toEpochMilliseconds() - (durationMinutes * 60 * 1000L)
            if (triggerMillis > now) {
                val task = AlarmTask.Reminder(
                    reminderId = reminder.id,
                    title = reminder.title.ifBlank { "التنبيه قبل الصلاة" },
                    body = reminder.description.ifBlank { "اقترب موعد الأذان، استعد للوضوء والصلاة" },
                    typeName = ReminderType.BEFORE_PRAYER.name
                )
                alarmScheduler.schedule(requestCode, triggerMillis, task)
            }
        }
    }

    companion object {
        const val REMINDER_TYPE_EXTRA = "REMINDER_TYPE_EXTRA"
    }
}
