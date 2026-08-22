package dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets

import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetPrayer
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PrayerWidgetStateExtensionsTest {
    @Test
    fun `current prayer does not wrap before the first displayed prayer`() {
        val state = stateWithUpcoming(index = 0)

        assertNull(state.currentPrayer())
        assertEquals("Dhuhr", state.followingPrayer()?.name)
    }

    @Test
    fun `following prayer does not wrap after the final displayed prayer`() {
        val state = stateWithUpcoming(index = 4)

        assertEquals("Maghrib", state.currentPrayer()?.name)
        assertNull(state.followingPrayer())
    }

    @Test
    fun `current and following prayers surround the upcoming prayer between daily prayers`() {
        val state = stateWithUpcoming(index = 2)

        assertEquals("Dhuhr", state.currentPrayer()?.name)
        assertEquals("Maghrib", state.followingPrayer()?.name)
    }

    /**
     * Builds a widget state with one row marked as upcoming.
     *
     * @param index index of the prayer row that should be marked as upcoming.
     * @return widget state containing five ordered prayer rows.
     */
    private fun stateWithUpcoming(index: Int): PrayerWidgetUiState {
        val prayers = prayerNames.mapIndexed { rowIndex, name ->
            PrayerWidgetPrayer(
                name = name,
                time = "12:00 PM",
                iconRes = R.drawable.shalat_zhuhur,
                isUpcoming = rowIndex == index,
            )
        }
        return PrayerWidgetUiState(
            nextPrayerName = prayers[index].name,
            prayers = prayers,
        )
    }

    private val prayerNames = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
}
