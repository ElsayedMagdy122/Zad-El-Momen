package dev.sayed.mehrabalmomen.presentation.reciver

import android.app.ActivityOptions
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import dev.sayed.mehrabalmomen.presentation.screen.azan.AzanActivity
import dev.sayed.mehrabalmomen.presentation.service.PrayerAlarmService
import dev.sayed.mehrabalmomen.presentation.utils.Constants.PRAYER_NAME_KEY
import dev.sayed.mehrabalmomen.presentation.utils.Constants.PRAYER_TIME_MILLIS_KEY

class AzanAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prayerName =
            intent?.getStringExtra(PRAYER_NAME_KEY) ?: "FAJR"
        val prayerTimeMillis =
            intent?.getLongExtra(PRAYER_TIME_MILLIS_KEY, System.currentTimeMillis()) ?: System.currentTimeMillis()

        try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            @Suppress("DEPRECATION")
            val wakeLock = powerManager?.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE,
                "ZadElMomen:AzanWakeLock"
            )
            wakeLock?.acquire(10000L)
        } catch (_: Exception) {
        }

        val azanIntent = Intent(context, AzanActivity::class.java).apply {
            addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP or
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            )
            putExtra(PRAYER_NAME_KEY, prayerName)
            putExtra(PRAYER_TIME_MILLIS_KEY, prayerTimeMillis)
        }

        val options = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ActivityOptions.makeBasic().apply {
                setPendingIntentBackgroundActivityStartMode(ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
            }.toBundle()
        } else {
            null
        }

        try {
            context.startActivity(azanIntent, options)
        } catch (_: Exception) {
        }

        val serviceIntent = Intent(context, PrayerAlarmService::class.java).apply {
            putExtra(PRAYER_NAME_KEY, prayerName)
            putExtra(PRAYER_TIME_MILLIS_KEY, prayerTimeMillis)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        dev.sayed.mehrabalmomen.presentation.widget.PrayerTimesWidgetProvider.updateAllWidgets(context)
    }
}
