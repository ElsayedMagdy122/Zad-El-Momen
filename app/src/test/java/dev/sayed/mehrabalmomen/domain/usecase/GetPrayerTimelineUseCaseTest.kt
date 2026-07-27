package dev.sayed.mehrabalmomen.domain.usecase

import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerCalculationError
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerCalculationException
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerSettings
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerTimelineResult
import dev.sayed.mehrabalmomen.domain.repository.TimeRepository
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerRepository
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class GetPrayerTimelineUseCaseTest {
    private val settings = PrayerSettings(
        madhab = Madhab.SHAFI,
        calculationMethod = CalculationMethod.EGYPTIAN,
        location = Location(latitude = 30.0444, longitude = 31.2357),
    )

    @Test
    fun `reads current instant and timezone once per calculation`() {
        val timeRepository = FakeTimeRepository(
            instant = Instant.parse("2026-01-15T02:00:00Z"),
            timeZone = TimeZone.UTC,
        )
        val repository = RecordingPrayerRepository(::prayersFor)
        val useCase = createUseCase(repository, timeRepository)

        useCase(settings)

        assertEquals(1, timeRepository.instantReads)
        assertEquals(1, timeRepository.timeZoneReads)
    }

    @Test
    fun `exactly Cairo midnight derives the new local date`() {
        val timeRepository = FakeTimeRepository(
            instant = Instant.parse("2026-01-14T22:00:00Z"),
            timeZone = TimeZone.of("Africa/Cairo"),
        )
        val repository = RecordingPrayerRepository(::prayersFor)
        val useCase = createUseCase(repository, timeRepository)

        val result = useCase(settings)

        assertEquals(LocalDate(2026, 1, 15), repository.requestedDates.first())
        assertEquals(LocalDate(2026, 1, 15), result.displayedDate)
    }

    @Test
    fun `before Isha requests only the current date`() {
        val timeRepository = FakeTimeRepository(
            instant = Instant.parse("2026-01-15T10:00:00Z"),
            timeZone = TimeZone.UTC,
        )
        val repository = RecordingPrayerRepository(::prayersFor)
        val useCase = createUseCase(repository, timeRepository)

        val result = useCase(settings)

        assertEquals(listOf(LocalDate(2026, 1, 15)), repository.requestedDates)
        assertEquals(Prayer.PrayerName.ASR, result.nextPrayer.name)
    }

    @Test
    fun `exactly Isha delegates once and returns repository timeline`() {
        val timeRepository = FakeTimeRepository(
            instant = Instant.parse("2026-01-15T17:00:00Z"),
            timeZone = TimeZone.UTC,
        )
        val repository = RecordingPrayerRepository(::prayersFor)
        val useCase = createUseCase(repository, timeRepository)

        val result = useCase(settings)

        assertEquals(listOf(LocalDate(2026, 1, 15)), repository.requestedDates)
        assertEquals(LocalDate(2026, 1, 16), result.displayedDate)
        assertEquals(Prayer.PrayerName.FAJR, result.nextPrayer.name)
    }

    @Test
    fun `moving the clock recomputes the next prayer`() {
        val timeRepository = FakeTimeRepository(
            instant = Instant.parse("2026-01-15T10:00:00Z"),
            timeZone = TimeZone.UTC,
        )
        val repository = RecordingPrayerRepository(::prayersFor)
        val useCase = createUseCase(repository, timeRepository)

        assertEquals(Prayer.PrayerName.ASR, useCase(settings).nextPrayer.name)
        timeRepository.instant = Instant.parse("2026-01-15T16:00:00Z")
        assertEquals(Prayer.PrayerName.ISHA, useCase(settings).nextPrayer.name)
        timeRepository.instant = Instant.parse("2026-01-15T02:00:00Z")
        assertEquals(Prayer.PrayerName.FAJR, useCase(settings).nextPrayer.name)
    }

    @Test
    fun `changing timezone recomputes the requested local date`() {
        val timeRepository = FakeTimeRepository(
            instant = Instant.parse("2026-01-15T00:30:00Z"),
            timeZone = TimeZone.UTC,
        )
        val repository = RecordingPrayerRepository(::prayersFor)
        val useCase = createUseCase(repository, timeRepository)

        useCase(settings)
        assertEquals(LocalDate(2026, 1, 15), repository.requestedDates.first())

        repository.requestedDates.clear()
        timeRepository.timeZone = TimeZone.of("America/Los_Angeles")
        useCase(settings)

        assertEquals(LocalDate(2026, 1, 14), repository.requestedDates.first())
    }

    @Test
    fun `repository calculation failure is propagated unchanged`() {
        val expectedFailure = PrayerCalculationException(
            error = PrayerCalculationError.CALCULATION_FAILED,
            message = "fixture failure",
        )
        val timeRepository = FakeTimeRepository(
            instant = Instant.parse("2026-01-15T02:00:00Z"),
            timeZone = TimeZone.UTC,
        )
        val repository = RecordingPrayerRepository(::prayersFor).apply {
            failure = expectedFailure
        }
        val useCase = createUseCase(repository, timeRepository)

        val actualFailure = assertThrows(PrayerCalculationException::class.java) {
            useCase(settings)
        }

        assertSame(expectedFailure, actualFailure)
    }

    /**
     * Creates the coordinator under test with explicit fake boundaries.
     *
     * @param prayerRepository repository that records requested dates.
     * @param timeRepository mutable time source used to exercise clock and timezone changes.
     * @return fully constructed Stage 2 coordinator.
     */
    private fun createUseCase(
        prayerRepository: PrayerRepository,
        timeRepository: TimeRepository,
    ): GetPrayerTimelineUseCase = GetPrayerTimelineUseCase(
        prayerRepository = prayerRepository,
        timeRepository = timeRepository,
    )

    /**
     * Produces a deterministic prayer list whose instants use the supplied date at UTC.
     *
     * @param date local date requested by the coordinator.
     * @return complete chronological fixture for that date.
     */
    private fun prayersFor(date: LocalDate): List<Prayer> {
        val dateText = date.toString()
        return listOf(
            Prayer(Prayer.PrayerName.FAJR, Instant.parse("${dateText}T03:00:00Z")),
            Prayer(Prayer.PrayerName.ZUHR, Instant.parse("${dateText}T09:00:00Z")),
            Prayer(Prayer.PrayerName.ASR, Instant.parse("${dateText}T12:00:00Z")),
            Prayer(Prayer.PrayerName.MAGHRIB, Instant.parse("${dateText}T15:00:00Z")),
            Prayer(Prayer.PrayerName.ISHA, Instant.parse("${dateText}T17:00:00Z")),
        )
    }

    /** Mutable test time source that also records how often each value is read. */
    private class FakeTimeRepository(
        var instant: Instant,
        var timeZone: TimeZone,
    ) : TimeRepository {
        var instantReads: Int = 0
        var timeZoneReads: Int = 0

        /** Returns the configured instant and increments its read counter. */
        override fun currentInstant(): Instant {
            instantReads += 1
            return instant
        }

        /** Returns the configured timezone and increments its read counter. */
        override fun currentTimeZone(): TimeZone {
            timeZoneReads += 1
            return timeZone
        }
    }

    /** Prayer repository fake that records every requested date and can inject a controlled error. */
    private class RecordingPrayerRepository(
        private val prayerFactory: (LocalDate) -> List<Prayer>,
    ) : PrayerRepository {
        val requestedDates = mutableListOf<LocalDate>()
        var failure: PrayerCalculationException? = null

        /** The coordinator must request a timeline rather than calculate daily prayers itself. */
        override fun getDailyPrayers(
            madhab: Madhab,
            calculationMethod: CalculationMethod,
            location: Location,
            date: LocalDate,
        ): List<Prayer> = error("GetPrayerTimelineUseCase must not call getDailyPrayers().")

        /** Records [date], propagates an injected failure, and returns a deterministic timeline. */
        override fun getPrayerTimeline(
            instant: Instant,
            madhab: Madhab,
            calculationMethod: CalculationMethod,
            location: Location,
            date: LocalDate,
        ): PrayerTimelineResult {
            requestedDates += date
            failure?.let { exception -> throw exception }
            val todayPrayers = prayerFactory(date)
            val nextPrayerToday = todayPrayers.firstOrNull { prayer -> prayer.time > instant }
            val displayedDate = if (nextPrayerToday != null) {
                date
            } else {
                date.plus(1, DateTimeUnit.DAY)
            }
            val displayedPrayers = if (nextPrayerToday != null) {
                todayPrayers
            } else {
                prayerFactory(displayedDate)
            }
            val nextPrayer = nextPrayerToday ?: displayedPrayers.first { prayer ->
                prayer.name == Prayer.PrayerName.FAJR
            }

            return PrayerTimelineResult(
                displayedDate = displayedDate,
                displayedPrayers = displayedPrayers,
                nextPrayer = nextPrayer,
                remainingDuration = (nextPrayer.time - instant).coerceAtLeast(Duration.ZERO),
            )
        }

        /** This coordinator never calls the legacy API, so use is treated as a test failure. */
        override fun getNextPrayer(
            instant: Instant,
            madhab: Madhab,
            calculationMethod: CalculationMethod,
            location: Location,
            date: LocalDate,
        ): Prayer = error("GetPrayerTimelineUseCase must not call getNextPrayer().")
    }
}
