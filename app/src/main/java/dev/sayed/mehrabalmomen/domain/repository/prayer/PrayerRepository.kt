package dev.sayed.mehrabalmomen.domain.repository.prayer

import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerCalculationException
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerTimelineResult
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

interface PrayerRepository {
    /**
     * Calculates the five obligatory prayers for one date and set of prayer settings.
     *
     * @param madhab jurisprudence used to calculate Asr.
     * @param calculationMethod convention used to calculate Fajr, Isha, and related adjustments.
     * @param location latitude and longitude used by the prayer calculation.
     * @param date local calendar date for which prayers are requested.
     * @return five prayers in canonical chronological order.
     * @throws PrayerCalculationException when coordinates or calculated prayer data are invalid.
     */
    fun getDailyPrayers(
        madhab: Madhab,
        calculationMethod: CalculationMethod,
        location: Location,
        date: LocalDate
    ): List<Prayer>

    /**
     * Calculates the complete prayer timeline for one instant and its associated local date.
     *
     * Today's schedule is returned while a prayer remains. At or after Isha, tomorrow's schedule
     * and Fajr are returned instead. Prayer selection uses strict absolute-instant comparison.
     *
     * @param instant absolute instant used to select the upcoming prayer.
     * @param madhab jurisprudence used to calculate Asr.
     * @param calculationMethod convention used for prayer calculations.
     * @param location coordinates used for daily calculations.
     * @param date local calendar date associated with [instant].
     * @return displayed date, ordered schedule, exact next prayer, and remaining duration.
     * @throws PrayerCalculationException when a required daily calculation fails.
     */
    @OptIn(ExperimentalTime::class)
    fun getPrayerTimeline(
        instant: Instant,
        madhab: Madhab,
        calculationMethod: CalculationMethod,
        location: Location,
        date: LocalDate,
    ): PrayerTimelineResult

    /**
     * Finds the first prayer strictly later than [instant] for the supplied date and settings.
     *
     * When no prayer remains on [date], this function calculates and returns the following day's
     * Fajr. Equality is treated as passed, so calling at exactly Fajr returns Zuhr.
     *
     * @param instant absolute instant used for the strict upcoming-prayer comparison.
     * @param madhab jurisprudence used to calculate Asr.
     * @param calculationMethod convention used for prayer calculations.
     * @param location coordinates used by the prayer calculation.
     * @param date local date associated with [instant].
     * @return the next prayer whose absolute time is later than [instant].
     * @throws PrayerCalculationException when a required daily calculation fails.
     */
    @OptIn(ExperimentalTime::class)
    fun getNextPrayer(
        instant: Instant,
        madhab: Madhab,
        calculationMethod: CalculationMethod,
        location: Location,
        date: LocalDate
    ): Prayer
}
