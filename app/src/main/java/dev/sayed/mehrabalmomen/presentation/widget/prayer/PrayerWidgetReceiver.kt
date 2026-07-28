package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.appwidget.AppWidgetManager
import android.content.Context
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
        PrayerWidgetProgressScheduler.from(context).schedule()
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
        PrayerWidgetProgressScheduler.from(context).schedule()
        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

    /**
     * Cancels widget-only periodic and exact scheduling after the final instance is removed.
     *
     * @param context receiver context used to resolve schedulers.
     */
    override fun onDisabled(context: Context) {
        PrayerWidgetProgressScheduler.from(context).cancel()
        GlobalContext.get().get<PrayerWidgetBoundaryScheduler>().cancel()
        super.onDisabled(context)
    }
}
