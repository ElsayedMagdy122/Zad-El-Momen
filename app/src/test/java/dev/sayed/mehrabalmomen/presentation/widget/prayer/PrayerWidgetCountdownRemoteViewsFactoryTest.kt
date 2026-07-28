package dev.sayed.mehrabalmomen.presentation.widget.prayer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrayerWidgetCountdownRemoteViewsFactoryTest {
    @Test
    fun `chronometer base uses elapsed realtime plus positive wall clock remaining time`() {
        val factory = PrayerWidgetCountdownRemoteViewsFactory(
            currentTimeMillis = { 1_000L },
            elapsedRealtimeMillis = { 5_000L },
        )

        assertEquals(9_000L, factory.chronometerBaseMillis(5_000L))
    }

    @Test
    fun `chronometer base clamps past targets to elapsed realtime now`() {
        val factory = PrayerWidgetCountdownRemoteViewsFactory(
            currentTimeMillis = { 5_000L },
            elapsedRealtimeMillis = { 9_000L },
        )

        assertEquals(9_000L, factory.chronometerBaseMillis(1_000L))
    }

    @Test
    fun `can start only accepts future targets`() {
        val factory = PrayerWidgetCountdownRemoteViewsFactory(
            currentTimeMillis = { 5_000L },
            elapsedRealtimeMillis = { 9_000L },
        )

        assertTrue(factory.canStart(5_001L))
        assertFalse(factory.canStart(5_000L))
        assertFalse(factory.canStart(4_999L))
        assertFalse(factory.canStart(null))
    }
}
