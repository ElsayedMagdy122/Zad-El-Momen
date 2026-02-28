package dev.sayed.mehrabalmomen.domain.usecase

import android.util.Log
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.Prayer
import dev.sayed.mehrabalmomen.domain.model.PrayerAlarm
import dev.sayed.mehrabalmomen.domain.repository.PrayerAlarmRepository
import dev.sayed.mehrabalmomen.domain.repository.PrayerNotificationsRepository
import dev.sayed.mehrabalmomen.domain.repository.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class PrayerSchedulingUseCase(
    private val settingsRepository: SettingsRepository,
    private val prayerRepository: PrayerRepository,
    private val schedulerRepository: PrayerAlarmRepository,
    private val notificationsRepository: PrayerNotificationsRepository,
) {

    suspend fun rescheduleTodayPrayerAlarms() {
        val today = Clock.System.todayIn(TimeZone.Companion.currentSystemDefault())
        val prayers = getDailyPrayers(today)
        val alarms = setupAlarms(prayers)
        schedulerRepository.reschedule(alarms)
    }

    private suspend fun getDailyPrayers(today: LocalDate): List<Prayer> {
        val settings = settingsRepository.observeAppSettings().first().prayerSettings
        return prayerRepository.getDailyPrayers(
            madhab = settings.madhab,
            calculationMethod = settings.calculationMethod,
            location = Location(
                latitude = settings.location.latitude,
                longitude = settings.location.longitude,
            ),
            date = today
        )
    }

    private suspend fun setupAlarms(prayers: List<Prayer>): List<PrayerAlarm> {
        val notifications = notificationsRepository.observeAll().first()
        return prayers.map { prayer ->
            PrayerAlarm(
                id = prayer.name.alarmId(),
                name = prayer.name,
                timeMillis = prayer.time.toEpochMilliseconds(),
                enabled = notifications[prayer.name] ?: true
            ).apply {
                Log.d("AZAN_DEBUG", "PrayerAlarm: $this")
            }
        }
    }

    private fun Prayer.PrayerName.alarmId(): Int = when (this) {
        Prayer.PrayerName.FAJR -> 10
        Prayer.PrayerName.ZUHR -> 20
        Prayer.PrayerName.ASR -> 30
        Prayer.PrayerName.MAGHRIB -> 40
        Prayer.PrayerName.ISHA -> 50
    }
}