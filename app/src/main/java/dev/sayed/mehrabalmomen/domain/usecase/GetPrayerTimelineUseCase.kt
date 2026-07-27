package dev.sayed.mehrabalmomen.domain.usecase

import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerCalculationException
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerSettings
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerTimelineResult
import dev.sayed.mehrabalmomen.domain.repository.TimeRepository
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerRepository
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

/** Coordinates time and prayer repositories to produce a domain prayer timeline. */
@OptIn(ExperimentalTime::class)
class GetPrayerTimelineUseCase(
    private val prayerRepository: PrayerRepository,
    private val timeRepository: TimeRepository,
) {
    /**
     * Calculates the prayer timeline for the current instant, timezone, and supplied settings.
     *
     * The instant and timezone are each read once, the current local date is derived from that
     * pair. The derived local date and settings are then passed to [prayerRepository], which owns
     * daily calculation, strict upcoming-prayer selection, and after-Isha rollover.
     *
     * @param settings Madhab, calculation method, and coordinates used for both daily calculations.
     * @return deterministic domain result for the date that should be displayed and its next prayer.
     * @throws PrayerCalculationException when [prayerRepository] rejects coordinates or cannot
     * produce a valid Adhan2 schedule. Repository exceptions are propagated unchanged.
     */
    operator fun invoke(settings: PrayerSettings): PrayerTimelineResult {
        val now = timeRepository.currentInstant()
        val timeZone = timeRepository.currentTimeZone()
        val today = now.toLocalDateTime(timeZone).date
        return prayerRepository.getPrayerTimeline(
            instant = now,
            madhab = settings.madhab,
            calculationMethod = settings.calculationMethod,
            location = settings.location,
            date = today,
        )
    }
}
