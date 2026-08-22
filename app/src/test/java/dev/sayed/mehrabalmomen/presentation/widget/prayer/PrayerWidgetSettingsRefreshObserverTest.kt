package dev.sayed.mehrabalmomen.presentation.widget.prayer

import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerSettings
import dev.sayed.mehrabalmomen.domain.entity.widget.prayer.PrayerWidgetSettings
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import org.junit.Assert.assertEquals
import org.junit.Test

class PrayerWidgetSettingsRefreshObserverTest {
    /**
     * Verifies that settings observation skips startup state and refreshes distinct changes only.
     *
     * @return no value; assertions fail if startup or duplicate emissions trigger refreshes.
     */
    @Test
    fun `observer skips initial settings and refreshes on distinct changes`() = runBlocking {
        val settingsFlow = MutableStateFlow(widgetSettings(language = AppSettings.Language.ENGLISH))
        var refreshes = 0
        val observer = PrayerWidgetSettingsRefreshObserver(settingsFlow) {
            refreshes += 1
        }

        val job = observer.start(this)
        yield()
        settingsFlow.value = widgetSettings(language = AppSettings.Language.ARABIC)
        yield()
        settingsFlow.value = widgetSettings(language = AppSettings.Language.ARABIC)
        yield()
        settingsFlow.value = widgetSettings(language = AppSettings.Language.ENGLISH)
        yield()
        job.cancelAndJoin()

        assertEquals(2, refreshes)
    }

    /**
     * Creates a complete widget settings value for observer tests.
     *
     * @param language app language carried by the widget settings emission.
     * @return widget settings with stable prayer calculation fields and configurable language.
     */
    private fun widgetSettings(language: AppSettings.Language): PrayerWidgetSettings {
        return PrayerWidgetSettings(
            prayerSettings = PrayerSettings(
                madhab = Madhab.SHAFI,
                calculationMethod = CalculationMethod.EGYPTIAN,
                location = Location(latitude = 30.0444, longitude = 31.2357),
            ),
            language = language,
            isLocationConfigured = true,
        )
    }
}
