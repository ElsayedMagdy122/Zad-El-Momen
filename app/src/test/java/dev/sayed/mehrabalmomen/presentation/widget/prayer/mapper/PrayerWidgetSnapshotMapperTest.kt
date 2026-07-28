package dev.sayed.mehrabalmomen.presentation.widget.prayer.mapper

import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.entity.widget.prayer.PrayerWidgetContent
import dev.sayed.mehrabalmomen.domain.entity.widget.prayer.PrayerWidgetSnapshot
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PrayerWidgetSnapshotMapperTest {
    private val mapper = PrayerWidgetSnapshotMapper()

    @Test
    fun `ready snapshot maps Arabic localized names digits and rtl`() {
        val state = mapper.map(
            PrayerWidgetSnapshot.Ready(
                content = content(language = AppSettings.Language.ARABIC),
            ),
        )

        assertEquals(PrayerWidgetStatus.READY, state.status)
        assertEquals("العصر", state.nextPrayerName)
        assertEquals("٠٣:٣٠ مساء", state.nextPrayerTime)
        assertEquals("٠١:٠٢:٠٣", state.countdown)
        assertEquals("ar", state.languageCode)
        assertTrue(state.isRtl)
        assertTrue(state.prayers.first { it.name == "العصر" }.isUpcoming)
    }

    @Test
    fun `permission required keeps calculated content`() {
        val state = mapper.map(
            PrayerWidgetSnapshot.PermissionRequired(
                content = content(language = AppSettings.Language.ENGLISH),
            ),
        )

        assertEquals(PrayerWidgetStatus.EXACT_ALARM_PERMISSION_REQUIRED, state.status)
        assertEquals("Asr", state.nextPrayerName)
        assertEquals(5, state.prayers.size)
    }

    @Test
    fun `noon and midnight use correct am and pm labels`() {
        val state = mapper.map(
            PrayerWidgetSnapshot.Ready(
                content = content(
                    language = AppSettings.Language.ENGLISH,
                    prayers = listOf(
                        Prayer(Prayer.PrayerName.FAJR, Instant.parse("2026-01-15T00:00:00Z")),
                        Prayer(Prayer.PrayerName.ZUHR, Instant.parse("2026-01-15T12:00:00Z")),
                    ),
                    nextPrayer = Prayer(
                        Prayer.PrayerName.ZUHR,
                        Instant.parse("2026-01-15T12:00:00Z"),
                    ),
                ),
            ),
        )

        assertEquals("12:00 AM", state.prayers[0].time)
        assertEquals("12:00 PM", state.prayers[1].time)
        assertEquals("12:00 PM", state.nextPrayerTime)
        assertFalse(state.isRtl)
    }

    @Test
    fun `upcoming comparison uses exact prayer instant`() {
        val todayFajr = Prayer(Prayer.PrayerName.FAJR, Instant.parse("2026-01-15T03:00:00Z"))
        val tomorrowFajr = Prayer(Prayer.PrayerName.FAJR, Instant.parse("2026-01-16T03:00:00Z"))
        val state = mapper.map(
            PrayerWidgetSnapshot.Ready(
                content = content(
                    displayedDate = LocalDate(2026, 1, 16),
                    prayers = listOf(todayFajr, tomorrowFajr),
                    nextPrayer = tomorrowFajr,
                ),
            ),
        )

        assertFalse(state.prayers[0].isUpcoming)
        assertTrue(state.prayers[1].isUpcoming)
        assertTrue(state.isTomorrow)
    }

    @Test
    fun `non content snapshots map to explicit statuses`() {
        assertEquals(
            PrayerWidgetStatus.NEEDS_LOCATION,
            mapper.map(PrayerWidgetSnapshot.NeedsLocation).status,
        )
        assertEquals(
            PrayerWidgetStatus.ERROR,
            mapper.map(
                PrayerWidgetSnapshot.Error(
                    dev.sayed.mehrabalmomen.domain.entity.widget.prayer.PrayerWidgetError.UNKNOWN,
                ),
            ).status,
        )
    }

    private fun content(
        language: AppSettings.Language = AppSettings.Language.ENGLISH,
        displayedDate: LocalDate = LocalDate(2026, 1, 15),
        prayers: List<Prayer> = defaultPrayers(),
        nextPrayer: Prayer = prayers.first { it.name == Prayer.PrayerName.ASR },
    ): PrayerWidgetContent {
        return PrayerWidgetContent(
            calculatedAt = Instant.parse("2026-01-15T14:27:57Z"),
            timeZone = TimeZone.UTC,
            currentLocalDate = LocalDate(2026, 1, 15),
            displayedDate = displayedDate,
            prayers = prayers,
            nextPrayer = nextPrayer,
            remainingDuration = 1.hours + 2.minutes + 3.seconds,
            location = Location(latitude = 30.0444, longitude = 31.2357),
            language = language,
        )
    }

    private fun defaultPrayers(): List<Prayer> {
        return listOf(
            Prayer(Prayer.PrayerName.FAJR, Instant.parse("2026-01-15T03:00:00Z")),
            Prayer(Prayer.PrayerName.ZUHR, Instant.parse("2026-01-15T12:00:00Z")),
            Prayer(Prayer.PrayerName.ASR, Instant.parse("2026-01-15T15:30:00Z")),
            Prayer(Prayer.PrayerName.MAGHRIB, Instant.parse("2026-01-15T18:00:00Z")),
            Prayer(Prayer.PrayerName.ISHA, Instant.parse("2026-01-15T19:30:00Z")),
        )
    }
}
