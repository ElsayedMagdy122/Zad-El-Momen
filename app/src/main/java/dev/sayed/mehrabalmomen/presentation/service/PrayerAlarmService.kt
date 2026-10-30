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
import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerStatus
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.utils.Constants.ACTION_STOP_AZAN
import dev.sayed.mehrabalmomen.presentation.utils.Constants.ACTION_STOP_AZAN_UI
import dev.sayed.mehrabalmomen.presentation.utils.Constants.PRAYER_NAME_KEY
import dev.sayed.mehrabalmomen.presentation.utils.Constants.PRAYER_TIME_MILLIS_KEY
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.koin.android.ext.android.inject

/**
 * Foreground service responsible for playing the Azan (Call to Prayer) audio.
 * It provides a silent, low-priority ongoing notification purely to satisfy
 * Android's foreground service requirements without annoying heads-up alerts.
 */
class PrayerAlarmService : Service() {

    private val audioPlayer: AudioPlayer by inject()
    private val settingsRepository: SettingsRepository by inject()
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onBind(intent: Intent?) = null

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_AZAN) {
            stopAzanInternal()
            return START_NOT_STICKY
        }

        createNotificationChannel()

        val prayerName = intent?.getStringExtra(PRAYER_NAME_KEY) ?: "FAJR"
        val prayerTimeMillis = intent?.getLongExtra(PRAYER_TIME_MILLIS_KEY, 0L) ?: 0L
        val prayerEnum = runCatching { Prayer.PrayerName.valueOf(prayerName) }
            .getOrDefault(Prayer.PrayerName.FAJR)

        // Start foreground with a quiet, non-intrusive notification (no heads-up popup)
        startForeground(NOTIFICATION_ID, createSilentNotification(prayerEnum, prayerName, prayerTimeMillis))

        playAzan()

        return START_NOT_STICKY
    }

    private fun stopAzanInternal() {
        audioPlayer.stop()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager?.cancel(NOTIFICATION_ID)

        try {
            val stopUiIntent = Intent(ACTION_STOP_AZAN_UI).apply {
                setPackage(packageName)
            }
            sendBroadcast(stopUiIntent)
        } catch (_: Exception) {
        }

        stopSelf()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Low importance: does NOT pop up over the screen, does NOT vibrate, does NOT make sound
            val channel = NotificationChannel(
                SILENT_AZAN_CHANNEL_ID,
                "صوت الأذان",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setSound(null, null)
                enableVibration(false)
                enableLights(false)
                setShowBadge(false)
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    @SuppressLint("FullScreenIntentPolicy")
    private fun createSilentNotification(
        prayerEnum: Prayer.PrayerName,
        prayerName: String,
        prayerTimeMillis: Long
    ): Notification {
        // Stop action
        val stopIntent = Intent(this, PrayerAlarmService::class.java).apply {
            action = ACTION_STOP_AZAN
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val title = "أذان ${prayerEnum.getDisplayName()}"

        return NotificationCompat.Builder(this, SILENT_AZAN_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText("جارٍ تشغيل أذان ${prayerEnum.getDisplayName()}")
            .setSmallIcon(R.drawable.ic_mosque_02)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOngoing(true)
            .setAutoCancel(false)
            .setSilent(true)
            .addAction(R.drawable.ic_close_circle, "إيقاف", stopPendingIntent)
            .build()
    }

    private fun playAzan() {
        serviceScope.launch {
            val selectedMoazen = settingsRepository.observeSelectedMoazen().first()
                .removeSuffix(".mp3")
            audioPlayer.play(AudioSource.LocalResource(selectedMoazen))
        }

        serviceScope.launch {
            audioPlayer.playerState.collect { state ->
                if (state.status == AudioPlayerStatus.ENDED) {
                    stopAzanInternal()
                }
            }
        }
    }

    override fun onDestroy() {
        instance = null
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
        private const val SILENT_AZAN_CHANNEL_ID = "azan_playback_silent_channel"

        @Volatile
        private var instance: PrayerAlarmService? = null

        fun isRunning(): Boolean = instance != null

        fun stopAzan() {
            instance?.stopAzanInternal()
        }
    }
}
