package dev.sayed.mehrabalmomen.presentation.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Build
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.presentation.utils.Constants
import dev.sayed.mehrabalmomen.presentation.utils.Constants.AZAN_CHANNEL_ID
import dev.sayed.mehrabalmomen.presentation.utils.Constants.AZAN_CHANNEL_NAME
import dev.sayed.mehrabalmomen.presentation.utils.Constants.PRAYER_NAME_KEY
import dev.sayed.mehrabalmomen.presentation.base.MainActivity

class PrayerAlarmService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    override fun onBind(intent: Intent?) = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.action == Constants.ACTION_STOP_AZAN) {
            stopAzan()
            return START_STICKY
        }
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            return START_NOT_STICKY
        }
        createChannel()
        val prayerName = intent?.getStringExtra(PRAYER_NAME_KEY) ?: return START_NOT_STICKY
        val prayerEnum = Prayer.PrayerName.valueOf(intent.getStringExtra(PRAYER_NAME_KEY) ?: "FAJR")
      //  val prayerNameLocalized = getString(prayerEnum.getDisplayNameRes())
        startForeground(1, createNotification(prayerEnum.getDisplayNameRes()))
        playAzan()

        return START_NOT_STICKY
    }

    private fun stopAzan() {
        if (::mediaPlayer.isInitialized && mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        stopForeground(true)
        stopSelf()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AZAN_CHANNEL_ID,
                AZAN_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(null, null)
                enableVibration(false)
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }

    @SuppressLint("FullScreenIntentPolicy")
    private fun createNotification(prayerName: String): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("FROM_ALARM", true)
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this, 0, openAppIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, PrayerAlarmService::class.java).apply {
            action = Constants.ACTION_STOP_AZAN
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 1, stopIntent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val bm = BitmapFactory.decodeResource(resources, R.drawable.night_mosque)

        return NotificationCompat.Builder(this, AZAN_CHANNEL_ID)
            .setFullScreenIntent(openAppPendingIntent, true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentTitle("أذان $prayerName")
            .setContentText("اضغط هنا لرؤية مواقيت الصلاة")
            .setSmallIcon(R.drawable.mosque_02)
          //  .setLargeIcon(bm)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(R.drawable.ic_close_circle, "إيقاف", stopPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun playAzan() {
        mediaPlayer = MediaPlayer().apply {
            setWakeMode(this@PrayerAlarmService, PowerManager.PARTIAL_WAKE_LOCK)
            setDataSource(resources.openRawResourceFd(R.raw.azan))
            prepare()
            start()

            setOnCompletionListener {
                stopForeground(true)
                stopSelf()
            }
        }
    }
    override fun onDestroy() {
        if (::mediaPlayer.isInitialized) {
            mediaPlayer.release()
        }
        super.onDestroy()
    }
    fun   Prayer.PrayerName.getDisplayNameRes(): String = when(this) {
          Prayer.PrayerName.FAJR -> "الفجر"
          Prayer.PrayerName.ZUHR -> "الظهر"
          Prayer.PrayerName.ASR -> "العصر"
          Prayer.PrayerName.MAGHRIB -> "المغرب"
        Prayer.PrayerName.ISHA -> "العشاء"
    }
}