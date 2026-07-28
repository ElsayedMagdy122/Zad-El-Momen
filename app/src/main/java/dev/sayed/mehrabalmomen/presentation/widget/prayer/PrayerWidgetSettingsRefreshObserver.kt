package dev.sayed.mehrabalmomen.presentation.widget.prayer

import dev.sayed.mehrabalmomen.domain.entity.widget.prayer.PrayerWidgetSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

/**
 * Observes saved prayer-widget settings and refreshes installed widgets after real changes.
 *
 * @property settingsFlow source of location, calculation, Madhab, language, and setup state.
 * @property refreshAllIfInstalled callback that refreshes widgets only when launcher instances exist.
 */
class PrayerWidgetSettingsRefreshObserver(
    private val settingsFlow: Flow<PrayerWidgetSettings>,
    private val refreshAllIfInstalled: suspend () -> Unit,
) {
    /**
     * Starts collection in the supplied application scope.
     *
     * The first DataStore emission represents current app state, so it is skipped to avoid a
     * duplicate refresh during process start. Later distinct changes refresh installed widgets.
     *
     * @param scope application-lifetime coroutine scope that owns the settings collection.
     * @return job representing the active settings observer.
     */
    fun start(scope: CoroutineScope): Job {
        return scope.launch {
            settingsFlow.distinctUntilChanged()
                .drop(1)
                .collect {
                    refreshAllIfInstalled()
                }
        }
    }
}
