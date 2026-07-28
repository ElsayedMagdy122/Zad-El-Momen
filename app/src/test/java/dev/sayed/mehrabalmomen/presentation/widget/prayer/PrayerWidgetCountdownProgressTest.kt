package dev.sayed.mehrabalmomen.presentation.widget.prayer

import org.junit.Assert.assertEquals
import org.junit.Test
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PrayerWidgetCountdownProgressTest {
    @Test
    fun `progress is empty at interval start`() {
        assertEquals(0, progressAt(currentEpochMillis = 1_000L))
    }

    @Test
    fun `progress is half full at interval midpoint`() {
        assertEquals(5_000, progressAt(currentEpochMillis = 3_000L))
    }

    @Test
    fun `progress is full at interval target`() {
        assertEquals(10_000, progressAt(currentEpochMillis = 5_000L))
    }

    @Test
    fun `progress clamps outside valid interval`() {
        assertEquals(0, progressAt(currentEpochMillis = 0L))
        assertEquals(10_000, progressAt(currentEpochMillis = 6_000L))
    }

    @Test
    fun `progress safely handles missing and invalid boundaries`() {
        assertEquals(0, calculatePrayerWidgetCountdownProgress(null, 5_000L, 3_000L))
        assertEquals(0, calculatePrayerWidgetCountdownProgress(1_000L, null, 3_000L))
        assertEquals(0, calculatePrayerWidgetCountdownProgress(5_000L, 1_000L, 3_000L))
        assertEquals(0, calculatePrayerWidgetCountdownProgress(1_000L, 1_000L, 1_000L))
    }

    @Test
    fun `overnight prayer interval uses absolute instants`() {
        val start = Instant.parse("2026-01-15T20:00:00Z").toEpochMilliseconds()
        val target = Instant.parse("2026-01-16T04:00:00Z").toEpochMilliseconds()
        val midnight = Instant.parse("2026-01-16T00:00:00Z").toEpochMilliseconds()

        assertEquals(
            5_000,
            calculatePrayerWidgetCountdownProgress(start, target, midnight),
        )
    }

    @Test
    fun `sweep angle follows clamped countdown progress`() {
        assertEquals(0f, prayerWidgetCountdownSweepAngle(-1))
        assertEquals(180f, prayerWidgetCountdownSweepAngle(5_000))
        assertEquals(360f, prayerWidgetCountdownSweepAngle(20_000))
    }

    /**
     * Calculates progress for the shared four-second test interval.
     *
     * @param currentEpochMillis current instant to evaluate inside or around the interval.
     * @return countdown progress in the inclusive `0..10000` range.
     */
    private fun progressAt(currentEpochMillis: Long): Int {
        return calculatePrayerWidgetCountdownProgress(
            startEpochMillis = 1_000L,
            targetEpochMillis = 5_000L,
            currentEpochMillis = currentEpochMillis,
        )
    }
}
