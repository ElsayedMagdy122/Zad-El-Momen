package dev.sayed.mehrabalmomen.data

import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.entity.Prayer
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.model.PrayerAlarm
import dev.sayed.mehrabalmomen.domain.repository.AzanSchedulerRepository
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
    private val schedulerRepository: AzanSchedulerRepository
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
        val enabledMap = mapOf(
            Prayer.PrayerName.ASR to false
        )
        return prayers.toPrayerAlarms(enabledMap)
    }

    private fun logAlarms(alarms: List<PrayerAlarm>) {
        alarms.forEach { alarm ->
            val formatted = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                .format(Date(alarm.timeMillis))
            android.util.Log.d("AthanReschedule", "Alarm for ${alarm.id} scheduled at $formatted")
        }
    }
}