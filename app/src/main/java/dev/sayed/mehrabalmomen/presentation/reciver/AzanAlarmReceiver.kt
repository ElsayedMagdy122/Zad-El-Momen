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

        // 1. Wake up the screen immediately
        try {
            val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
            @Suppress("DEPRECATION")
            val wakeLock = powerManager?.newWakeLock(
                PowerManager.SCREEN_BRIGHT_WAKE_LOCK or
                        PowerManager.ACQUIRE_CAUSES_WAKEUP or
                        PowerManager.ON_AFTER_RELEASE,
                "ZadElMomen:AzanWakeLock"
            )
            wakeLock?.acquire(10000L) // 10 seconds to allow Activity to present
        } catch (_: Exception) {
        }

        // 2. Immediately launch AzanActivity directly from the AlarmManager receiver exemption
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

        // 3. Start the foreground service to play the Azan audio and maintain notification
        val serviceIntent = Intent(context, PrayerAlarmService::class.java).apply {
            putExtra(PRAYER_NAME_KEY, prayerName)
            putExtra(PRAYER_TIME_MILLIS_KEY, prayerTimeMillis)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
