package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

/** Receives exact widget alarms and refreshes every installed prayer widget. */
class PrayerWidgetBoundaryReceiver : BroadcastReceiver() {
    /**
     * Starts an asynchronous all-widget refresh for expected explicit widget alarm broadcasts.
     *
     * The refreshed Ready snapshot schedules the following boundary and local midnight through
     * [PrayerWidget].
     * Unknown actions are ignored without allocating background work.
     *
     * @param context receiver context used to update installed widgets.
     * @param intent delivered broadcast whose action identifies the widget refresh cause.
     */
    override fun onReceive(context: Context, intent: Intent?) {
        if (!intent.isWidgetAlarmAction()) return

        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                GlobalContext.get().get<PrayerWidgetUpdateCoordinator>().refreshAll()
            } finally {
                pendingResult.finish()
            }
        }
    }

    /**
     * Checks whether this intent belongs to one of the private widget exact-alarm actions.
     *
     * @receiver broadcast intent delivered to [PrayerWidgetBoundaryReceiver], if present.
     * @return `true` for prayer-boundary or local-midnight widget alarms, otherwise `false`.
     */
    private fun Intent?.isWidgetAlarmAction(): Boolean {
        return this?.action == ACTION_PRAYER_WIDGET_BOUNDARY ||
            this?.action == ACTION_PRAYER_WIDGET_LOCAL_MIDNIGHT
    }

    companion object {
        /** Explicit private broadcast action identifying a prayer-widget boundary refresh. */
        const val ACTION_PRAYER_WIDGET_BOUNDARY =
            "dev.sayed.mehrabalmomen.action.PRAYER_WIDGET_BOUNDARY"

        /** Explicit private broadcast action identifying a prayer-widget local-midnight refresh. */
        const val ACTION_PRAYER_WIDGET_LOCAL_MIDNIGHT =
            "dev.sayed.mehrabalmomen.action.PRAYER_WIDGET_LOCAL_MIDNIGHT"
    }
}
