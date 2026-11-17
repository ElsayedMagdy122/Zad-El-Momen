package dev.sayed.mehrabalmomen.presentation.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import dev.sayed.mehrabalmomen.domain.repository.platform.WidgetPinProvider

class WidgetPinProviderImpl(private val context: Context) : WidgetPinProvider {
    override fun requestPinWidget(onShowToast: (title: String, message: String) -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val provider = ComponentName(context, "dev.sayed.mehrabalmomen.presentation.widget.PrayerTimesWidgetProvider")
            if (appWidgetManager != null && appWidgetManager.isRequestPinAppWidgetSupported) {
                val successIntent = Intent(context, Class.forName("dev.sayed.mehrabalmomen.presentation.widget.PrayerTimesWidgetPinReceiver"))
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
            "ويدجت مواقيت الصلاة",
            "لإضافة الويدجت، اضغط مطولاً على الشاشة الرئيسية، ثم اختر التطبيق."
        )
    }
}
