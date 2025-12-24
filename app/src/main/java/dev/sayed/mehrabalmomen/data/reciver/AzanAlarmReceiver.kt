package dev.sayed.mehrabalmomen.data.reciver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.data.Constants.AZAN_CHANNEL_ID
import dev.sayed.mehrabalmomen.data.Constants.AZAN_CHANNEL_NAME
import dev.sayed.mehrabalmomen.data.Constants.PRAYER_NAME_KEY

class AzanAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prayerName =
            intent?.getStringExtra(PRAYER_NAME_KEY) ?: "Unknown"
        showNotification(context, prayerName)

//        val serviceIntent = Intent(context, AzanService::class.java).apply {
//            putExtra(PRAYER_NAME_KEY, prayerName)
//        }
//        ContextCompat.startForegroundService(context, serviceIntent)

    }

    private fun showNotification(context: Context, prayerName: String) {

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (nm.getNotificationChannel(AZAN_CHANNEL_ID) == null) {
                val channel = NotificationChannel(
                    AZAN_CHANNEL_ID,
                    AZAN_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                )
                nm.createNotificationChannel(channel)
            }
        }

        val notification = Notification.Builder(context, AZAN_CHANNEL_ID)
            .setContentTitle("وقت الصلاة")
            .setContentText("حان الآن وقت صلاة $prayerName")
            .setSmallIcon(R.drawable.app_icon)
            .build()
        nm.notify(prayerName.hashCode(), notification)
    }
}