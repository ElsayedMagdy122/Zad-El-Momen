package dev.sayed.mehrabalmomen.data.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import androidx.core.app.NotificationCompat
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.data.Constants
import dev.sayed.mehrabalmomen.presentation.base.MainActivity

class AzanService : Service() {

    private lateinit var mediaPlayer: MediaPlayer

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val prayerName = intent?.getStringExtra(Constants.PRAYER_NAME_KEY) ?: "Prayer"

        val notification = createNotification(prayerName)
        startForeground(1, notification)


        mediaPlayer = MediaPlayer.create(this, R.raw.azan)
        mediaPlayer.isLooping = false
        mediaPlayer.start()

        mediaPlayer.setOnCompletionListener {
            stopForeground(true)
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun createNotification(prayerName: String): Notification {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("FROM_ALARM", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val bm = BitmapFactory.decodeResource(resources, R.drawable.night_mosque)

        return NotificationCompat.Builder(this, Constants.AZAN_CHANNEL_ID)
            .setContentTitle("أذان $prayerName")
            .setContentText("اضغط هنا لرؤية مواقيت الصلاة")
            .setSmallIcon(R.drawable.mosque_02)
            .setContentIntent(pendingIntent)
            .setStyle(NotificationCompat.BigPictureStyle().bigPicture(bm))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
           // .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(false)
            .build()
    }
    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
    }
}