package dev.sayed.mehrabalmomen.data.prayer.repository

import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerCalculationError
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerCalculationException
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerTimelineResult
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PrayerRepositoryImplTest {
    private val repository = PrayerRepositoryImpl()
    private val cairo = Location(latitude = 30.0444, longitude = 31.2357)
    private val fixtureDate = LocalDate(2026, 1, 15)

    @Test
    fun `Cairo Egyptian Shafi calculation matches deterministic fixture`() {
        val prayers = dailyPrayers(
            location = cairo,
            date = fixtureDate,
            method = CalculationMethod.EGYPTIAN,
            madhab = Madhab.SHAFI,
        )

        assertEquals(
            listOf(
                "2026-01-15T03:21:00Z",
                "2026-01-15T10:05:00Z",
                "2026-01-15T12:58:00Z",
                "2026-01-15T15:17:00Z",
                "2026-01-15T16:39:00Z",
            ),
            prayers.map { prayer -> prayer.time.toString() },
        )
    }

    @Test
    fun `daily calculation returns canonical names in strict instant order`() {
        val prayers = dailyPrayers()

        assertEquals(Prayer.PrayerName.entries, prayers.map { prayer -> prayer.name })
        assertTrue(
            prayers.zipWithNext().all { (current, following) -> current.time < following.time },
        )
    }

    @Test
    fun `every calculation method returns a usable five-prayer result`() {
        CalculationMethod.entries.forEach { method ->
            val prayers = try {
                dailyPrayers(method = method)
            } catch (exception: PrayerCalculationException) {
                throw AssertionError("Calculation method $method produced an unusable result.", exception)
            }

            assertEquals("Unexpected count for $method", 5, prayers.size)
            assertEquals(
                "Unexpected names for $method",
                Prayer.PrayerName.entries,
                prayers.map { prayer -> prayer.name },
            )
        }
    }

    @Test
    fun `Hanafi Asr is later than Shafi Asr for Cairo fixture`() {
        val shafiAsr = dailyPrayers(madhab = Madhab.SHAFI)
            .first { prayer -> prayer.name == Prayer.PrayerName.ASR }
        val hanafiAsr = dailyPrayers(madhab = Madhab.HANAFI)
            .first { prayer -> prayer.name == Prayer.PrayerName.ASR }

        assertTrue(hanafiAsr.time > shafiAsr.time)
        assertNotEquals(shafiAsr.time, hanafiAsr.time)
    }

    @Test
    fun `before Fajr timeline selects Fajr and displays today`() {
        val todayPrayers = dailyPrayers()

        val timeline = timelineAt(todayPrayers.first().time - 1.milliseconds)

        assertEquals(Prayer.PrayerName.FAJR, timeline.nextPrayer.name)
        assertEquals(fixtureDate, timeline.displayedDate)
        assertEquals(todayPrayers, timeline.displayedPrayers)
    }

    @Test
    fun `one millisecond before Fajr reports one millisecond remaining`() {
        val fajr = dailyPrayers().first()

        val timeline = timelineAt(fajr.time - 1.milliseconds)

        assertEquals(fajr, timeline.nextPrayer)
        assertEquals(1.milliseconds, timeline.remainingDuration)
    }

    @Test
    fun `exactly Fajr timeline advances to Zuhr`() {
        val todayPrayers = dailyPrayers()

        val timeline = timelineAt(todayPrayers.first().time)

        assertEquals(Prayer.PrayerName.ZUHR, timeline.nextPrayer.name)
    }

    @Test
    fun `one millisecond after Fajr timeline selects Zuhr`() {
        val todayPrayers = dailyPrayers()

        val timeline = timelineAt(todayPrayers.first().time + 1.milliseconds)

        assertEquals(Prayer.PrayerName.ZUHR, timeline.nextPrayer.name)
    }

    @Test
    fun `every interval timeline selects the later prayer`() {
        val todayPrayers = dailyPrayers()

        todayPrayers.zipWithNext().forEach { (current, following) ->
            val timeline = timelineAt(current.time + 1.milliseconds)

            assertEquals(following, timeline.nextPrayer)
        }
    }

    @Test
    fun `exactly Isha timeline displays tomorrow and selects tomorrow Fajr`() {
        val todayPrayers = dailyPrayers()
        val tomorrowDate = fixtureDate.plus(1, DateTimeUnit.DAY)
        val tomorrowPrayers = dailyPrayers(date = tomorrowDate)

        val timeline = timelineAt(todayPrayers.last().time)

        assertEquals(tomorrowDate, timeline.displayedDate)
        assertEquals(tomorrowPrayers, timeline.displayedPrayers)
        assertEquals(tomorrowPrayers.first(), timeline.nextPrayer)
    }

    @Test
    fun `after Isha timeline displays tomorrow and selects tomorrow Fajr`() {
        val todayPrayers = dailyPrayers()
        val tomorrowDate = fixtureDate.plus(1, DateTimeUnit.DAY)
        val tomorrowPrayers = dailyPrayers(date = tomorrowDate)

        val timeline = timelineAt(todayPrayers.last().time + 1.milliseconds)

        assertEquals(tomorrowDate, timeline.displayedDate)
        assertEquals(tomorrowPrayers, timeline.displayedPrayers)
        assertEquals(tomorrowPrayers.first(), timeline.nextPrayer)
    }

    @Test
    fun `timeline upcoming comparison includes prayer instant`() {
        val todayFajr = dailyPrayers().first()
        val timeline = timelineAt(dailyPrayers().last().time + 1.milliseconds)

        assertTrue(!timeline.isUpcoming(todayFajr))
        assertTrue(timeline.isUpcoming(timeline.nextPrayer))
    }

    @Test
    fun `timeline remaining duration is never negative`() {
        val todayPrayers = dailyPrayers()
        val testInstants = todayPrayers.map { prayer -> prayer.time } +
            (todayPrayers.last().time + 1.milliseconds)

        testInstants.forEach { instant ->
            assertTrue(timelineAt(instant).remainingDuration >= Duration.ZERO)
        }
    }

    @Test
    fun `after Isha next prayer is following date Fajr`() {
        val todayPrayers = dailyPrayers()
        val tomorrowFajr = dailyPrayers(date = fixtureDate.plus(1, DateTimeUnit.DAY)).first()

        val nextPrayer = repository.getNextPrayer(
            instant = todayPrayers.last().time + 1.milliseconds,
            madhab = Madhab.SHAFI,
            calculationMethod = CalculationMethod.EGYPTIAN,
            location = cairo,
            date = fixtureDate,
        )

        assertEquals(tomorrowFajr, nextPrayer)
    }

    @Test
    fun `valid inclusive coordinate boundaries reach calculation instead of range validation`() {
        val boundaryLocations = listOf(
            Location(latitude = 90.0, longitude = 0.0),
            Location(latitude = -90.0, longitude = 0.0),
            Location(latitude = 0.0, longitude = 180.0),
            Location(latitude = 0.0, longitude = -180.0),
        )

        boundaryLocations.forEach { location ->
            val result = runCatching { dailyPrayers(location = location) }
            result.onSuccess { prayers -> assertEquals(5, prayers.size) }
            result.onFailure { exception ->
                assertTrue(exception is PrayerCalculationException)
                exception as PrayerCalculationException
                assertNotEquals(PrayerCalculationError.INVALID_LATITUDE, exception.error)
                assertNotEquals(PrayerCalculationError.INVALID_LONGITUDE, exception.error)
            }
        }
    }

    @Test
    fun `non-finite and out-of-range coordinates return typed validation errors`() {
        val cases = listOf(
            Location(Double.NaN, 0.0) to PrayerCalculationError.INVALID_LATITUDE,
            Location(Double.POSITIVE_INFINITY, 0.0) to PrayerCalculationError.INVALID_LATITUDE,
            Location(-90.0001, 0.0) to PrayerCalculationError.INVALID_LATITUDE,
            Location(90.0001, 0.0) to PrayerCalculationError.INVALID_LATITUDE,
            Location(0.0, Double.NaN) to PrayerCalculationError.INVALID_LONGITUDE,
            Location(0.0, Double.NEGATIVE_INFINITY) to PrayerCalculationError.INVALID_LONGITUDE,
            Location(0.0, -180.0001) to PrayerCalculationError.INVALID_LONGITUDE,
            Location(0.0, 180.0001) to PrayerCalculationError.INVALID_LONGITUDE,
        )

        cases.forEach { (location, expectedError) ->
            val exception = assertThrows(PrayerCalculationException::class.java) {
                dailyPrayers(location = location)
            }
            assertEquals(expectedError, exception.error)
        }
    }

    @Test
    fun `high-latitude calculation cannot leak a raw library exception`() {
        val exception = assertThrows(PrayerCalculationException::class.java) {
            dailyPrayers(
                location = Location(latitude = 90.0, longitude = 0.0),
                date = LocalDate(2026, 6, 21),
            )
        }

        assertTrue(
            exception.error == PrayerCalculationError.CALCULATION_FAILED ||
                exception.error == PrayerCalculationError.INVALID_PRAYER_RESULT,
        )
    }

    @Test
    fun `identical calculation inputs return identical results`() {
        val first = dailyPrayers()
        val second = dailyPrayers()

        assertEquals(first, second)
    }

    /**
     * Calculates a prayer fixture while allowing each test to override one input dimension.
     *
     * @param location coordinates passed to the real Adhan2-backed repository.
     * @param date local date for which prayer instants are calculated.
     * @param method calculation convention used by Adhan2.
     * @param madhab jurisprudence used for Asr.
     * @return validated five-prayer domain list.
     */
    private fun dailyPrayers(
        location: Location = cairo,
        date: LocalDate = fixtureDate,
        method: CalculationMethod = CalculationMethod.EGYPTIAN,
        madhab: Madhab = Madhab.SHAFI,
    ): List<Prayer> = repository.getDailyPrayers(
        madhab = madhab,
        calculationMethod = method,
        location = location,
        date = date,
    )

    /**
     * Calculates the public repository timeline for the standard Cairo fixture.
     *
     * @param instant absolute instant used for strict upcoming-prayer selection.
     * @return timeline produced by the repository's private resolver.
     */
    private fun timelineAt(instant: Instant): PrayerTimelineResult = repository.getPrayerTimeline(
        instant = instant,
        madhab = Madhab.SHAFI,
        calculationMethod = CalculationMethod.EGYPTIAN,
        location = cairo,
        date = fixtureDate,
    )
}
