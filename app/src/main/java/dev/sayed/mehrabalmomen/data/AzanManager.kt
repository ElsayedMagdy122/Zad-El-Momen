package dev.sayed.mehrabalmomen.data

import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.entity.Prayer
import dev.sayed.mehrabalmomen.domain.model.PrayerAlarm
import dev.sayed.mehrabalmomen.domain.repository.AzanSchedulerRepository
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
class AzanManager(
    private val settingsRepository: SettingsRepository,
    private val prayerRepository: PrayerRepository,
    private val schedulerRepository: AzanSchedulerRepository,
    private val notificationsRepository: PrayerNotificationsRepository,
) {

    suspend fun rescheduleTodayPrayerAlarms() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        val prayers = getDailyPrayers(today)

//        val baseTime = System.currentTimeMillis() + 1_000L
//
//        val testAlarms = prayers.mapIndexed { index, prayer ->
//            PrayerAlarm(
//                id = prayer.name.ordinal,
//                name = prayer.name,
//                timeMillis = baseTime + (index * 5_000L),
//                enabled = notifications[prayer.name] ?: true
//            )
//        }
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
                id = prayer.name.ordinal,
                name = prayer.name,
                timeMillis = prayer.time.toEpochMilliseconds(),
                enabled = notifications[prayer.name] ?: true
            )
        }
    }
}