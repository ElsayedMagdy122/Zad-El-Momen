package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Receives the exact prayer-boundary alarm and refreshes every installed prayer widget. */
class PrayerWidgetBoundaryReceiver : BroadcastReceiver() {
    /**
     * Starts an asynchronous all-widget refresh for the expected explicit boundary broadcast.
     *
     * The refreshed Ready snapshot schedules the following boundary through [PrayerWidget].
     * Unknown actions are ignored without allocating background work.
     *
     * @param context receiver context used to update installed widgets.
     * @param intent delivered broadcast whose action identifies a prayer boundary.
     */
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_PRAYER_WIDGET_BOUNDARY) return

        val pendingResult = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                PrayerWidget().updateAll(context.applicationContext)
            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        /** Explicit private broadcast action identifying a prayer-widget boundary refresh. */
        const val ACTION_PRAYER_WIDGET_BOUNDARY =
            "dev.sayed.mehrabalmomen.action.PRAYER_WIDGET_BOUNDARY"
    }
}
