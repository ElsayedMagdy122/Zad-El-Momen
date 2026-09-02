package dev.sayed.mehrabalmomen.presentation.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.utils.Constants
import dev.sayed.mehrabalmomen.presentation.utils.Constants.AZAN_CHANNEL_ID
import dev.sayed.mehrabalmomen.presentation.utils.Constants.AZAN_CHANNEL_NAME
import dev.sayed.mehrabalmomen.presentation.utils.Constants.PRAYER_NAME_KEY
import dev.sayed.mehrabalmomen.presentation.base.MainActivity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.koin.android.ext.android.inject

/**
 * Foreground service responsible for playing the Azan (Call to Prayer) and displaying
 * the associated notification. It uses the unified [AudioPlayer] for playback.
 */
class PrayerAlarmService : Service() {

    private val audioPlayer: AudioPlayer by inject()
    private val settingsRepository: SettingsRepository by inject()
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onBind(intent: Intent?) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == Constants.ACTION_STOP_AZAN) {
            stopAzan()
            return START_NOT_STICKY
        }

        createNotificationChannel()

        val prayerName = intent?.getStringExtra(PRAYER_NAME_KEY) ?: "FAJR"
        val prayerEnum = runCatching { Prayer.PrayerName.valueOf(prayerName) }.getOrDefault(Prayer.PrayerName.FAJR)

        startForeground(NOTIFICATION_ID, createNotification(prayerEnum.getDisplayName()))
        playAzan()

        return START_NOT_STICKY
    }

    private fun stopAzan() {
        audioPlayer.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                AZAN_CHANNEL_ID,
                AZAN_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setSound(null, null)
                enableVibration(false)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    @SuppressLint("FullScreenIntentPolicy")
    private fun createNotification(prayerDisplayName: String): Notification {
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

        return NotificationCompat.Builder(this, AZAN_CHANNEL_ID)
            .setFullScreenIntent(openAppPendingIntent, true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setContentTitle("أذان $prayerDisplayName")
            .setContentText("اضغط هنا لرؤية مواقيت الصلاة")
            .setSmallIcon(R.drawable.ic_mosque_02)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .addAction(R.drawable.ic_close_circle, "إيقاف", stopPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun playAzan() {
        serviceScope.launch {
            val selectedMoazen = settingsRepository.observeSelectedMoazen().first()
                .removeSuffix(".mp3")
            
            audioPlayer.play(AudioSource.LocalResource(selectedMoazen))
        }
    }

    override fun onDestroy() {
        audioPlayer.release()
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun Prayer.PrayerName.getDisplayName(): String = when (this) {
        Prayer.PrayerName.FAJR -> "الفجر"
        Prayer.PrayerName.ZUHR -> "الظهر"
        Prayer.PrayerName.ASR -> "العصر"
        Prayer.PrayerName.MAGHRIB -> "المغرب"
        Prayer.PrayerName.ISHA -> "العشاء"
    }

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}