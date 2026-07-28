package dev.sayed.mehrabalmomen.domain.usecase

import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerCalculationError
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerCalculationException
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerSettings
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerTimelineResult
import dev.sayed.mehrabalmomen.domain.entity.time.CurrentTimeContext
import dev.sayed.mehrabalmomen.domain.entity.widget.prayer.PrayerWidgetError
import dev.sayed.mehrabalmomen.domain.entity.widget.prayer.PrayerWidgetSettings
import dev.sayed.mehrabalmomen.domain.entity.widget.prayer.PrayerWidgetSnapshot
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.repository.TimeRepository
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.domain.repository.widget.ExactAlarmPermissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class GetPrayerWidgetSnapshotUseCaseTest {
    private val cairo = Location(latitude = 30.0444, longitude = 31.2357)

    @Test
    fun `unconfigured location returns NeedsLocation without reading time or calculating`() = runBlocking {
        val settingsRepository = FakeSettingsRepository(
            widgetSettings = widgetSettings(location = Location(0.0, 0.0), isConfigured = false),
        )
        val timeRepository = FakeTimeRepository()
        val prayerRepository = RecordingPrayerRepository()
        val useCase = createUseCase(settingsRepository, timeRepository, prayerRepository)

        val snapshot = useCase()

        assertEquals(PrayerWidgetSnapshot.NeedsLocation, snapshot)
        assertEquals(1, settingsRepository.widgetSettingsReads)
        assertEquals(0, timeRepository.timeContextReads)
        assertEquals(0, prayerRepository.timelineCalls)
    }

    @Test
    fun `legitimate zero coordinates are accepted when location is explicitly configured`() = runBlocking {
        val settingsRepository = FakeSettingsRepository(
            widgetSettings = widgetSettings(location = Location(0.0, 0.0), isConfigured = true),
        )
        val prayerRepository = RecordingPrayerRepository()
        val useCase = createUseCase(settingsRepository, prayerRepository = prayerRepository)

        val snapshot = useCase()

        assertTrue(snapshot is PrayerWidgetSnapshot.Ready)
        assertEquals(Location(0.0, 0.0), prayerRepository.lastLocation)
    }

    @Test
    fun `ready snapshot reads settings time and permission once`() = runBlocking {
        val settingsRepository = FakeSettingsRepository(widgetSettings = widgetSettings())
        val timeRepository = FakeTimeRepository()
        val prayerRepository = RecordingPrayerRepository()
        val permissionRepository = FakeExactAlarmPermissionRepository(canSchedule = true)
        val useCase = createUseCase(
            settingsRepository = settingsRepository,
            timeRepository = timeRepository,
            prayerRepository = prayerRepository,
            permissionRepository = permissionRepository,
        )

        val snapshot = useCase()

        assertTrue(snapshot is PrayerWidgetSnapshot.Ready)
        snapshot as PrayerWidgetSnapshot.Ready
        assertEquals(1, settingsRepository.widgetSettingsReads)
        assertEquals(1, timeRepository.timeContextReads)
        assertEquals(1, prayerRepository.timelineCalls)
        assertEquals(1, permissionRepository.reads)
        assertEquals(AppSettings.Language.ARABIC, snapshot.content.language)
        assertEquals(TimeZone.UTC, snapshot.content.timeZone)
        assertEquals(LocalDate(2026, 1, 15), snapshot.content.currentLocalDate)
        assertEquals(
            Instant.parse("2026-01-16T00:00:00Z"),
            snapshot.content.nextLocalMidnight,
        )
    }

    @Test
    fun `missing exact alarm permission returns PermissionRequired with calculated content`() = runBlocking {
        val useCase = createUseCase(
            permissionRepository = FakeExactAlarmPermissionRepository(canSchedule = false),
        )

        val snapshot = useCase()

        assertTrue(snapshot is PrayerWidgetSnapshot.PermissionRequired)
        snapshot as PrayerWidgetSnapshot.PermissionRequired
        assertEquals(Prayer.PrayerName.ASR, snapshot.content.nextPrayer.name)
    }

    @Test
    fun `changing settings recalculates the snapshot`() = runBlocking {
        val settingsRepository = FakeSettingsRepository(widgetSettings = widgetSettings())
        val prayerRepository = RecordingPrayerRepository()
        val useCase = createUseCase(
            settingsRepository = settingsRepository,
            prayerRepository = prayerRepository,
        )

        val first = useCase() as PrayerWidgetSnapshot.Ready
        settingsRepository.widgetSettings = widgetSettings(
            location = Location(latitude = 21.3891, longitude = 39.8579),
            madhab = Madhab.HANAFI,
            calculationMethod = CalculationMethod.UMM_AL_QURA,
        )
        val second = useCase() as PrayerWidgetSnapshot.Ready

        assertEquals(2, prayerRepository.timelineCalls)
        assertEquals(Location(latitude = 21.3891, longitude = 39.8579), prayerRepository.lastLocation)
        assertEquals(Madhab.HANAFI, prayerRepository.lastMadhab)
        assertEquals(CalculationMethod.UMM_AL_QURA, prayerRepository.lastCalculationMethod)
        assertTrue(second.content.prayers.first().time > first.content.prayers.first().time)
        assertTrue(second.content.prayers.first { it.name == Prayer.PrayerName.ASR }.time >
            first.content.prayers.first { it.name == Prayer.PrayerName.ASR }.time)
    }

    @Test
    fun `two calls on opposite sides of midnight do not retain a stale date`() = runBlocking {
        val timeRepository = FakeTimeRepository(
            instant = Instant.parse("2026-01-15T21:59:59Z"),
            timeZone = TimeZone.of("Africa/Cairo"),
        )
        val prayerRepository = RecordingPrayerRepository()
        val useCase = createUseCase(
            timeRepository = timeRepository,
            prayerRepository = prayerRepository,
        )

        useCase()
        timeRepository.instant = Instant.parse("2026-01-15T22:00:01Z")
        val snapshot = useCase() as PrayerWidgetSnapshot.Ready

        assertEquals(
            listOf(LocalDate(2026, 1, 15), LocalDate(2026, 1, 16)),
            prayerRepository.requestedDates,
        )
        assertEquals(LocalDate(2026, 1, 16), snapshot.content.currentLocalDate)
    }

    /**
     * Verifies that the widget snapshot rolls from Isha to the next day's Fajr.
     *
     * @return no value; assertions fail if the next prayer or midnight target is stale.
     */
    @Test
    fun `after Isha snapshot targets tomorrow Fajr and next local midnight`() = runBlocking {
        val useCase = createUseCase(
            timeRepository = FakeTimeRepository(
                instant = Instant.parse("2026-01-15T18:00:00Z"),
                timeZone = TimeZone.UTC,
            ),
        )

        val snapshot = useCase() as PrayerWidgetSnapshot.Ready

        assertEquals(Prayer.PrayerName.FAJR, snapshot.content.nextPrayer.name)
        assertEquals(LocalDate(2026, 1, 16), snapshot.content.displayedDate)
        assertEquals(
            Instant.parse("2026-01-16T00:00:00Z"),
            snapshot.content.nextLocalMidnight,
        )
    }

    /**
     * Verifies that the widget's midnight alarm uses the captured calculation timezone.
     *
     * @return no value; assertions fail if midnight is calculated in UTC by mistake.
     */
    @Test
    fun `next local midnight uses captured timezone`() = runBlocking {
        val useCase = createUseCase(
            timeRepository = FakeTimeRepository(
                instant = Instant.parse("2026-01-15T20:00:00Z"),
                timeZone = TimeZone.of("Africa/Cairo"),
            ),
        )

        val snapshot = useCase() as PrayerWidgetSnapshot.Ready

        assertEquals(
            Instant.parse("2026-01-15T22:00:00Z"),
            snapshot.content.nextLocalMidnight,
        )
    }

    @Test
    fun `repository calculation exception returns controlled error snapshot`() = runBlocking {
        val prayerRepository = RecordingPrayerRepository().apply {
            failure = PrayerCalculationException(
                error = PrayerCalculationError.INVALID_LATITUDE,
                message = "invalid fixture",
            )
        }
        val useCase = createUseCase(prayerRepository = prayerRepository)

        val snapshot = useCase()

        assertEquals(
            PrayerWidgetSnapshot.Error(PrayerWidgetError.INVALID_LOCATION),
            snapshot,
        )
    }

    private fun createUseCase(
        settingsRepository: FakeSettingsRepository = FakeSettingsRepository(widgetSettings()),
        timeRepository: FakeTimeRepository = FakeTimeRepository(),
        prayerRepository: RecordingPrayerRepository = RecordingPrayerRepository(),
        permissionRepository: FakeExactAlarmPermissionRepository =
            FakeExactAlarmPermissionRepository(canSchedule = true),
    ): GetPrayerWidgetSnapshotUseCase {
        val timelineUseCase = GetPrayerTimelineUseCase(
            prayerRepository = prayerRepository,
            timeRepository = timeRepository,
        )
        return GetPrayerWidgetSnapshotUseCase(
            settingsRepository = settingsRepository,
            timeRepository = timeRepository,
            getPrayerTimelineUseCase = timelineUseCase,
            exactAlarmPermissionRepository = permissionRepository,
        )
    }

    private fun widgetSettings(
        location: Location = cairo,
        madhab: Madhab = Madhab.SHAFI,
        calculationMethod: CalculationMethod = CalculationMethod.EGYPTIAN,
        language: AppSettings.Language = AppSettings.Language.ARABIC,
        isConfigured: Boolean = true,
    ): PrayerWidgetSettings {
        return PrayerWidgetSettings(
            prayerSettings = PrayerSettings(
                madhab = madhab,
                calculationMethod = calculationMethod,
                location = location,
            ),
            language = language,
            isLocationConfigured = isConfigured,
        )
    }

    private class FakeSettingsRepository(
        var widgetSettings: PrayerWidgetSettings,
    ) : SettingsRepository {
        var widgetSettingsReads = 0

        override suspend fun saveMadhab(madhab: Madhab) = Unit
        override suspend fun saveCalculationMethod(method: CalculationMethod) = Unit
        override suspend fun saveLocation(location: Location) = Unit
        override suspend fun saveLanguage(language: AppSettings.Language) = Unit
        override suspend fun saveTheme(theme: AppSettings.Theme) = Unit
        override suspend fun setOnboardingComplete() = Unit
        override fun observeLocation(): Flow<Location> = flowOf(widgetSettings.prayerSettings.location)
        override fun observeOnboardingComplete(): Flow<Boolean> = flowOf(false)
        override fun observePrayerSettings(): Flow<PrayerSettings> = flowOf(widgetSettings.prayerSettings)
        override fun observePrayerWidgetSettings(): Flow<PrayerWidgetSettings> = flow {
            widgetSettingsReads += 1
            emit(widgetSettings)
        }
        override suspend fun saveQuranFontSize(size: Int) = Unit
        override fun observeQuranFontSize(): Flow<Int> = flowOf(20)
        override suspend fun saveSelectedMoazen(fileName: String) = Unit
        override fun observeSelectedMoazen(): Flow<String> = flowOf("")
        override suspend fun saveTafseer(type: String) = Unit
        override fun observeTafseer(): Flow<String> = flowOf("")
        override fun observeAppSettings(): Flow<AppSettings> = flowOf(
            AppSettings(
                prayerSettings = widgetSettings.prayerSettings,
                alarmsScheduled = false,
                language = widgetSettings.language,
            ),
        )
    }

    private class FakeTimeRepository(
        var instant: Instant = Instant.parse("2026-01-15T10:00:00Z"),
        var timeZone: TimeZone = TimeZone.UTC,
    ) : TimeRepository {
        var timeContextReads = 0

        override fun currentTimeContext(): CurrentTimeContext {
            timeContextReads += 1
            return CurrentTimeContext(
                instant = instant,
                timeZone = timeZone,
            )
        }
    }

    private class FakeExactAlarmPermissionRepository(
        var canSchedule: Boolean,
    ) : ExactAlarmPermissionRepository {
        var reads = 0

        override fun canScheduleExactAlarms(): Boolean {
            reads += 1
            return canSchedule
        }
    }

    private class RecordingPrayerRepository : PrayerRepository {
        val requestedDates = mutableListOf<LocalDate>()
        var timelineCalls = 0
        var lastLocation: Location? = null
        var lastMadhab: Madhab? = null
        var lastCalculationMethod: CalculationMethod? = null
        var failure: PrayerCalculationException? = null

        override fun getDailyPrayers(
            madhab: Madhab,
            calculationMethod: CalculationMethod,
            location: Location,
            date: LocalDate,
        ): List<Prayer> = prayersFor(date, madhab, calculationMethod)

        override fun getPrayerTimeline(
            instant: Instant,
            madhab: Madhab,
            calculationMethod: CalculationMethod,
            location: Location,
            date: LocalDate,
        ): PrayerTimelineResult {
            timelineCalls += 1
            requestedDates += date
            lastLocation = location
            lastMadhab = madhab
            lastCalculationMethod = calculationMethod
            failure?.let { throw it }

            val todayPrayers = prayersFor(date, madhab, calculationMethod)
            val nextPrayerToday = todayPrayers.firstOrNull { prayer -> prayer.time > instant }
            val displayedDate = if (nextPrayerToday == null) {
                date.plus(1, DateTimeUnit.DAY)
            } else {
                date
            }
            val displayedPrayers = if (nextPrayerToday == null) {
                prayersFor(displayedDate, madhab, calculationMethod)
            } else {
                todayPrayers
            }
            val nextPrayer = nextPrayerToday ?: displayedPrayers.first()

            return PrayerTimelineResult(
                displayedDate = displayedDate,
                displayedPrayers = displayedPrayers,
                nextPrayer = nextPrayer,
                countdownStartInstant = instant,
                remainingDuration = (nextPrayer.time - instant).coerceAtLeast(Duration.ZERO),
            )
        }

        override fun getNextPrayer(
            instant: Instant,
            madhab: Madhab,
            calculationMethod: CalculationMethod,
            location: Location,
            date: LocalDate,
        ): Prayer = error("Widget snapshot use case must use the timeline API.")

        private fun prayersFor(
            date: LocalDate,
            madhab: Madhab,
            calculationMethod: CalculationMethod,
        ): List<Prayer> {
            val methodOffsetHours = if (calculationMethod == CalculationMethod.EGYPTIAN) 0 else 1
            val asrHour = if (madhab == Madhab.HANAFI) 13 else 12
            val dateText = date.toString()
            return listOf(
                Prayer(Prayer.PrayerName.FAJR, instantAt(dateText, 3 + methodOffsetHours)),
                Prayer(Prayer.PrayerName.ZUHR, instantAt(dateText, 9 + methodOffsetHours)),
                Prayer(Prayer.PrayerName.ASR, instantAt(dateText, asrHour + methodOffsetHours)),
                Prayer(Prayer.PrayerName.MAGHRIB, instantAt(dateText, 15 + methodOffsetHours)),
                Prayer(Prayer.PrayerName.ISHA, instantAt(dateText, 17 + methodOffsetHours)),
            )
        }

        private fun instantAt(dateText: String, hour: Int): Instant {
            return Instant.parse(
                String.format(Locale.US, "%sT%02d:00:00Z", dateText, hour),
            )
        }
    }
}
