package dev.sayed.mehrabalmomen.domain.usecase

import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerCalculationException
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerSettings
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerTimelineResult
import dev.sayed.mehrabalmomen.domain.entity.time.CurrentTimeContext
import dev.sayed.mehrabalmomen.domain.repository.TimeRepository
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerRepository
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

/**
 * Coordinates time and prayer repositories to produce a domain prayer timeline.
 *
 * @property prayerRepository calculation boundary that builds daily prayer timelines.
 * @property timeRepository clock boundary used when the caller does not supply a time context.
 */
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
        return invoke(
            settings = settings,
            timeContext = timeRepository.currentTimeContext(),
        )
    }

    /**
     * Calculates a prayer timeline using an already captured time context.
     *
     * This overload lets a larger operation reuse exactly the same instant and timezone across
     * all derived values, preventing midnight or timezone changes from producing mixed results.
     *
     * @param settings Madhab, calculation method, and location used for the calculation.
     * @param timeContext instant and timezone that define the current local date.
     * @return the prayer list, next prayer, displayed date, and remaining duration.
     * @throws PrayerCalculationException when the repository rejects the location or calculation.
     */
    operator fun invoke(
        settings: PrayerSettings,
        timeContext: CurrentTimeContext,
    ): PrayerTimelineResult {
        val today = timeContext.instant.toLocalDateTime(timeContext.timeZone).date
        return prayerRepository.getPrayerTimeline(
            instant = timeContext.instant,
            madhab = settings.madhab,
            calculationMethod = settings.calculationMethod,
            location = settings.location,
            date = today,
        )
    }
}
