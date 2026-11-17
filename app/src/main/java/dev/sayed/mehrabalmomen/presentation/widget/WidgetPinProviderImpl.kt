package dev.sayed.mehrabalmomen.presentation.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.repository.platform.WidgetPinProvider

class WidgetPinProviderImpl(private val context: Context) : WidgetPinProvider {
    override fun requestPinWidget(onShowToast: (title: String, message: String) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val provider = ComponentName(context, PrayerTimesWidgetProvider::class.java)
            if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
                val successIntent = Intent(context, PrayerTimesWidgetPinReceiver::class.java)
                val successPendingIntent = PendingIntent.getBroadcast(
                    context,
                    0,
                    successIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                appWidgetManager.requestPinAppWidget(provider, null, successPendingIntent)
                return
            }
        }
        onShowToast(
            context.getString(R.string.prayer_times_widget),
            context.getString(R.string.widget_pin_manual_guide)
        )
    }
}
