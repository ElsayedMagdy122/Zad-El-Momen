package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll

/**
 * Coordinates shared prayer-widget refresh and widget-only scheduling work.
 *
 * @property context application context used by Glance and platform widget lookups.
 * @property progressScheduler unique WorkManager recovery scheduler for installed widgets.
 * @property boundaryScheduler exact prayer-boundary and local-midnight scheduler.
 */
class PrayerWidgetUpdateCoordinator(
    private val context: Context,
    private val progressScheduler: PrayerWidgetProgressScheduler,
    private val boundaryScheduler: PrayerWidgetBoundaryScheduler,
) {
    /**
     * Rebuilds and updates every installed prayer widget through the normal Glance pipeline.
     *
     * @return no value; after completion, installed widget instances have received fresh content.
     */
    suspend fun refreshAll() {
        PrayerWidget().updateAll(context)
    }

    /**
     * Rebuilds widgets only when at least one prayer-widget instance is currently installed.
     *
     * @return `true` when widgets were found and refreshed, or `false` when no instance exists.
     */
    suspend fun refreshAllIfInstalled(): Boolean {
        if (!hasInstalledWidgets()) return false

        refreshAll()
        return true
    }

    /**
     * Ensures the unique 15-minute recovery refresh is registered for installed widgets.
     *
     * @return no value; after completion, WorkManager owns one recovery job for the widget set.
     */
    fun scheduleRecovery() {
        progressScheduler.schedule()
    }

    /**
     * Cancels all widget-only recovery and exact scheduling after the final widget is removed.
     *
     * @return no value; after completion, no widget-only periodic, boundary, or midnight work stays.
     */
    fun cancelWidgetWork() {
        progressScheduler.cancel()
        boundaryScheduler.cancelAll()
    }

    /**
     * Checks whether the launcher currently hosts at least one prayer-widget instance.
     *
     * @return `true` when Glance reports installed prayer-widget IDs, otherwise `false`.
     */
    private suspend fun hasInstalledWidgets(): Boolean {
        return GlanceAppWidgetManager(context).getGlanceIds(PrayerWidget::class.java).isNotEmpty()
    }
}
