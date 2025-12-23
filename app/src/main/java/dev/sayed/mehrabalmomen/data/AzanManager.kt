@file:OptIn(ExperimentalTime::class)

package dev.sayed.mehrabalmomen.data

import android.util.Log
import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.entity.Prayer
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.model.PrayerAlarm
import dev.sayed.mehrabalmomen.domain.repository.AzanSchedulerRepository
import dev.sayed.mehrabalmomen.domain.repository.PrayerNotificationsRepository
import dev.sayed.mehrabalmomen.domain.repository.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AzanManager(
    private val settingsRepository: SettingsRepository,
    private val prayerRepository: PrayerRepository,
    private val schedulerRepository: AzanSchedulerRepository,
    private val notificationsRepository: PrayerNotificationsRepository,
) {

    @OptIn(ExperimentalTime::class)
    fun reschedule() {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val settings = getCurrentSettings()
        val prayers = getDailyPrayers(settings, today)
        val alarms = createAlarms(prayers)
        logAlarms(alarms)
        schedulerRepository.reschedule(alarms)
    }

    private fun getCurrentSettings() = runBlocking {
        settingsRepository.observeAll().first()
    }

    @OptIn(ExperimentalTime::class)
    private fun getDailyPrayers(settings: AppSettings, date: kotlinx.datetime.LocalDate) =
        runBlocking {
            prayerRepository.getDailyPrayers(
                madhab = settings.madhab,
                calculationMethod = settings.calculationMethod,
                location = Location(settings.latitude, settings.longitude),
                date = date
            )
        }

    private fun createAlarms(prayers: List<Prayer>): List<PrayerAlarm> {
        val enabledMap = runBlocking {
            notificationsRepository.observeAll().first()
        }

        return prayers.mapIndexed { index, prayer ->

            val offsetMillis = index * 5_000L

            PrayerAlarm(
                id = prayer.name.ordinal,
                name = prayer.name,
                timeMillis = prayer.time.toEpochMilliseconds() + offsetMillis,
                enabled = enabledMap[prayer.name] ?: true
            )
        }
    }

    private fun logAlarms(alarms: List<PrayerAlarm>) {
        alarms.forEach { alarm ->
            val formatted = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(Date(alarm.timeMillis))
            Log.d("AzanManager", "Alarm for ${alarm.name}: $formatted")
        }
    }
}