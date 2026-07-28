package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import org.koin.core.context.GlobalContext

/** Manual widget action that retries the prayer-widget refresh pipeline. */
class PrayerWidgetRefreshAction : ActionCallback {
    /**
     * Runs a full prayer-widget refresh after the user taps the manual retry surface.
     *
     * @param context Android context supplied by Glance for the tapped widget action.
     * @param glanceId identifier of the widget instance that triggered the action.
     * @param parameters optional Glance action parameters; no custom parameters are required.
     * @return no value; after completion, every installed prayer widget is updated.
     */
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        GlobalContext.get().get<PrayerWidgetUpdateCoordinator>().refreshAll()
    }
}
