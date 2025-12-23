package dev.sayed.mehrabalmomen.data.reciver

import dev.sayed.mehrabalmomen.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class AzanAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prayerName =
            intent?.getStringExtra("PRAYER_NAME") ?: "Unknown"
        Log.d("AzanAlarm", "Alarm received for prayer position $prayerName")
        showNotification(context, prayerName)

    }
    private fun showNotification(context: Context, prayerName: String) {

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "azan_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (nm.getNotificationChannel(channelId) == null) {
                val channel = NotificationChannel(channelId,"Azan", NotificationManager.IMPORTANCE_HIGH)
                nm.createNotificationChannel(channel)
            }
        }

        val notification = Notification.Builder(context, channelId)
            .setContentTitle("وقت الصلاة")
            .setContentText("حان الآن وقت صلاة $prayerName")
            .setSmallIcon(R.drawable.app_icon)
            .build()
        nm.notify(prayerName.hashCode(), notification)
    }
}