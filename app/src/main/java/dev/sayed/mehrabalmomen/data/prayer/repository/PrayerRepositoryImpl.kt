package dev.sayed.mehrabalmomen.data.prayer.repository

import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.data.DateComponents
import dev.sayed.mehrabalmomen.data.prayer.mapper.toAdhanMadhab
import dev.sayed.mehrabalmomen.data.prayer.mapper.toAdhanParams
import dev.sayed.mehrabalmomen.data.prayer.mapper.toPrayerList
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerCalculationError
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerCalculationException
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerTimelineResult
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerRepository
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Calculates prayer times with Adhan2 and converts library failures into domain failures. */
@OptIn(ExperimentalTime::class)
class PrayerRepositoryImpl : PrayerRepository {
    /**
     * Calculates and validates all five obligatory prayers for one local calendar date.
     *
     * Coordinates are validated before invoking Adhan2. The calculated result is then checked for
     * canonical names, unique instants, and strict chronological order before being returned.
     *
     * @param madhab jurisprudence used by Adhan2 to calculate Asr.
     * @param calculationMethod Adhan2 calculation convention used for the daily schedule.
     * @param location coordinates used for the astronomical calculation.
     * @param date local calendar date for which prayer instants are requested.
     * @return five validated prayers ordered Fajr, Zuhr, Asr, Maghrib, and Isha.
     * @throws PrayerCalculationException when coordinates are invalid, Adhan2 fails, or the
     * resulting schedule is incomplete, duplicated, or not chronological.
     */
    override fun getDailyPrayers(
        madhab: Madhab,
        calculationMethod: CalculationMethod,
        location: Location,
        date: LocalDate,
    ): List<Prayer> {
        validateLocation(location)
        val prayers = calculatePrayerTimes(
            location = location,
            date = date,
            madhab = madhab,
            calculationMethod = calculationMethod,
        ).toPrayerList()
        validatePrayerResult(prayers)
        return prayers
    }

    /**
     * Calculates the schedule, next prayer, and remaining duration for [instant].
     *
     * Today's prayers are calculated and then passed to the private timeline resolver. That
     * resolver calculates tomorrow only when no prayer remains today.
     *
     * @param instant absolute instant used for strict upcoming-prayer comparison.
     * @param madhab jurisprudence used to calculate Asr for both dates.
     * @param calculationMethod calculation convention used for both dates.
     * @param location coordinates used for both daily calculations.
     * @param date local calendar date associated with [instant].
     * @return complete prayer timeline for today or, after Isha, tomorrow.
     * @throws PrayerCalculationException when either daily calculation fails validation.
     */
    override fun getPrayerTimeline(
        instant: Instant,
        madhab: Madhab,
        calculationMethod: CalculationMethod,
        location: Location,
        date: LocalDate,
    ): PrayerTimelineResult {
        val todayPrayers = getDailyPrayers(
            madhab = madhab,
            calculationMethod = calculationMethod,
            location = location,
            date = date,
        )

        return resolvePrayerTimeline(
            now = instant,
            today = date,
            todayPrayers = todayPrayers,
            madhab = madhab,
            calculationMethod = calculationMethod,
            location = location,
        )
    }

    /**
     * Calculates the first prayer strictly later than [instant].
     *
     * This compatibility API delegates to [getPrayerTimeline], ensuring existing screens use the
     * same exact-boundary and after-Isha behavior as the widget timeline.
     *
     * @param instant absolute instant used to determine which prayers have passed.
     * @param madhab jurisprudence used to calculate Asr for both dates.
     * @param calculationMethod calculation convention used for both dates.
     * @param location coordinates used for both daily calculations.
     * @param date local calendar date associated with [instant].
     * @return first prayer with an instant strictly later than [instant].
     * @throws PrayerCalculationException when either daily calculation fails validation.
     */
    override fun getNextPrayer(
        instant: Instant,
        madhab: Madhab,
        calculationMethod: CalculationMethod,
        location: Location,
        date: LocalDate,
    ): Prayer = getPrayerTimeline(
        instant = instant,
        madhab = madhab,
        calculationMethod = calculationMethod,
        location = location,
        date = date,
    ).nextPrayer

    /**
     * Resolves which date and prayer schedule should be displayed for [now].
     *
     * Prayer inputs are sorted by absolute instant. The first prayer strictly later than [now] is
     * selected. When today's prayers have all passed, the function calculates the following date,
     * loads its validated schedule, and selects its Fajr. This function never reads system time or
     * formats localized values.
     *
     * @param now absolute instant at which the timeline is being calculated.
     * @param today local calendar date represented by [todayPrayers].
     * @param todayPrayers validated prayers calculated for [today].
     * @param madhab jurisprudence used if tomorrow must be calculated.
     * @param calculationMethod calculation convention used if tomorrow must be calculated.
     * @param location coordinates used if tomorrow must be calculated.
     * @return ordered timeline for today or, after today's final prayer, tomorrow.
     * @throws PrayerCalculationException when tomorrow is required but cannot be calculated.
     */
    private fun resolvePrayerTimeline(
        now: Instant,
        today: LocalDate,
        todayPrayers: List<Prayer>,
        madhab: Madhab,
        calculationMethod: CalculationMethod,
        location: Location,
    ): PrayerTimelineResult {
        val orderedToday = todayPrayers.sortedBy { prayer -> prayer.time }
        val nextPrayerTodayIndex = orderedToday.indexOfFirst { prayer -> prayer.time > now }

        val displayedDate: LocalDate
        val displayedPrayers: List<Prayer>
        val nextPrayer: Prayer
        val countdownStartInstant: Instant
        if (nextPrayerTodayIndex >= 0) {
            displayedDate = today
            displayedPrayers = orderedToday
            nextPrayer = orderedToday[nextPrayerTodayIndex]
            countdownStartInstant = if (nextPrayerTodayIndex > 0) {
                orderedToday[nextPrayerTodayIndex - 1].time
            } else {
                val yesterday = today.minus(1, DateTimeUnit.DAY)
                getDailyPrayers(
                    madhab = madhab,
                    calculationMethod = calculationMethod,
                    location = location,
                    date = yesterday,
                ).maxBy { prayer -> prayer.time }.time
            }
        } else {
            displayedDate = today.plus(1, DateTimeUnit.DAY)
            displayedPrayers = getDailyPrayers(
                madhab = madhab,
                calculationMethod = calculationMethod,
                location = location,
                date = displayedDate,
            ).sortedBy { prayer -> prayer.time }
            nextPrayer = displayedPrayers.first { prayer ->
                prayer.name == Prayer.PrayerName.FAJR
            }
            countdownStartInstant = orderedToday.last().time
        }

        return PrayerTimelineResult(
            displayedDate = displayedDate,
            displayedPrayers = displayedPrayers,
            nextPrayer = nextPrayer,
            countdownStartInstant = countdownStartInstant,
            remainingDuration = (nextPrayer.time - now).coerceAtLeast(Duration.ZERO),
        )
    }

    /**
     * Validates coordinates before they cross the Adhan2 boundary.
     *
     * @param location coordinate pair to validate.
     * @throws PrayerCalculationException when latitude or longitude is non-finite or outside its
     * inclusive geographic range.
     */
    private fun validateLocation(location: Location) {
        if (!location.latitude.isFinite() || location.latitude !in -90.0..90.0) {
            throw PrayerCalculationException(
                error = PrayerCalculationError.INVALID_LATITUDE,
                message = "Latitude must be finite and within -90.0..90.0: ${location.latitude}",
            )
        }
        if (!location.longitude.isFinite() || location.longitude !in -180.0..180.0) {
            throw PrayerCalculationException(
                error = PrayerCalculationError.INVALID_LONGITUDE,
                message = "Longitude must be finite and within -180.0..180.0: ${location.longitude}",
            )
        }
    }

    /**
     * Invokes Adhan2 with domain settings mapped to its calculation types.
     *
     * @param location already validated coordinates.
     * @param date local date passed to Adhan2 as date components.
     * @param madhab domain Madhab mapped to the Adhan2 equivalent.
     * @param calculationMethod domain method mapped to Adhan2 parameters.
     * @return raw Adhan2 [PrayerTimes] for later domain mapping and result validation.
     * @throws PrayerCalculationException when Adhan2 or a settings mapper throws an exception.
     */
    private fun calculatePrayerTimes(
        location: Location,
        date: LocalDate,
        madhab: Madhab,
        calculationMethod: CalculationMethod,
    ): PrayerTimes {
        return try {
            val coordinates = Coordinates(location.latitude, location.longitude)
            val dateComponents = DateComponents(
                date.year,
                date.month.number,
                date.day,
            )
            val calculationParameters = calculationMethod
                .toAdhanParams()
                .copy(madhab = madhab.toAdhanMadhab())

            PrayerTimes(
                coordinates = coordinates,
                dateComponents = dateComponents,
                calculationParameters = calculationParameters,
            )
        } catch (exception: Exception) {
            throw PrayerCalculationException(
                error = PrayerCalculationError.CALCULATION_FAILED,
                message = "Prayer calculation failed for $date at $location.",
                cause = exception,
            )
        }
    }

    /**
     * Verifies the structural guarantees promised by [getDailyPrayers].
     *
     * @param prayers mapped Adhan2 output in canonical prayer-name order.
     * @throws PrayerCalculationException when a name is missing, an instant is duplicated, or the
     * sequence is not strictly chronological.
     */
    private fun validatePrayerResult(prayers: List<Prayer>) {
        val expectedNames = Prayer.PrayerName.entries.toSet()
        val hasExpectedNames = prayers.size == expectedNames.size &&
            prayers.map { prayer -> prayer.name }.toSet() == expectedNames
        val hasUniqueInstants = prayers.map { prayer -> prayer.time }.distinct().size == prayers.size
        val isChronological = prayers.zipWithNext().all { (current, following) ->
            current.time < following.time
        }

        if (!hasExpectedNames || !hasUniqueInstants || !isChronological) {
            throw PrayerCalculationException(
                error = PrayerCalculationError.INVALID_PRAYER_RESULT,
                message = "Adhan2 returned an incomplete, duplicate, or unordered prayer schedule.",
            )
        }
    }
}
