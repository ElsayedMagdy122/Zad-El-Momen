package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Bundle
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import org.koin.core.context.GlobalContext

class PrayerWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = PrayerWidget()

    /**
     * Starts the shared periodic progress refresh when the first widget instance is enabled.
     *
     * @param context receiver context used to resolve WorkManager.
     */
    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        GlobalContext.get().get<PrayerWidgetUpdateCoordinator>().scheduleRecovery()
    }

    /**
     * Restores the unique periodic refresh whenever Android updates existing widget instances.
     *
     * This covers app upgrades and host restoration without creating work per widget ID.
     *
     * @param context receiver context used to resolve WorkManager.
     * @param appWidgetManager platform manager delivering the widget update.
     * @param appWidgetIds installed widget instance identifiers included in this update.
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        GlobalContext.get().get<PrayerWidgetUpdateCoordinator>().scheduleRecovery()
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    /**
     * Keeps widget-only recovery active when a launcher resize causes a widget rebuild.
     *
     * @param context receiver context used to resolve schedulers.
     * @param appWidgetManager platform manager delivering the resize event.
     * @param appWidgetId installed widget instance identifier that changed size.
     * @param newOptions size/options bundle supplied by the launcher host.
     * @return no value; after completion, recovery is scheduled and Glance handles the resize.
     */
    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle,
    ) {
        GlobalContext.get().get<PrayerWidgetUpdateCoordinator>().scheduleRecovery()
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    /**
     * Cancels widget-only periodic and exact scheduling after the final instance is removed.
     *
     * @param context receiver context used to resolve schedulers.
     */
    override fun onDisabled(context: Context) {
        GlobalContext.get().get<PrayerWidgetUpdateCoordinator>().cancelWidgetWork()
        super.onDisabled(context)
    }
}
