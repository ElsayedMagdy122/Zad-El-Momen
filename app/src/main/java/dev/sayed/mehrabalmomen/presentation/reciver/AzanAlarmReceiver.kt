package dev.sayed.mehrabalmomen.presentation.reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import dev.sayed.mehrabalmomen.presentation.service.PrayerAlarmService
import dev.sayed.mehrabalmomen.presentation.utils.Constants.PRAYER_NAME_KEY

class AzanAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prayerName =
            intent?.getStringExtra(PRAYER_NAME_KEY) ?: "Unknown"
        val serviceIntent = Intent(context, PrayerAlarmService::class.java).apply {
            putExtra(PRAYER_NAME_KEY, prayerName)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

    }
}