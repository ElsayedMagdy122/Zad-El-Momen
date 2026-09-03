package dev.sayed.mehrabalmomen.presentation.screen.radio.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerConstants.ACTION_SENDED
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerConstants.CHANNEL_ID
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerConstants.CHANNEL_NAME
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerConstants.MEDIA_FOREGROUND_ID
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerConstants.NOTIFICATION_TITLE
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.RadioPlayerConstants.STREAM_URL
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

/**
 * Service responsible for managing background radio playback.
 * It uses the unified [AudioPlayer] abstraction.
 */
class RadioPlayerService : Service() {

    private val audioPlayer: AudioPlayer by inject(named("radio"))
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val actionString = intent?.getStringExtra(ACTION_SENDED)
        val url = intent?.getStringExtra(STREAM_URL)
        val action = actionString?.let { runCatching { RadioPlayerAction.valueOf(it) }.getOrNull() }
        val titleText = intent?.getStringExtra(NOTIFICATION_TITLE) ?: "Quran Radio"

        when (action) {
            RadioPlayerAction.PLAY -> url?.let {
                showForegroundNotification(titleText)
                audioPlayer.play(AudioSource.fromPath(it))
            }

            RadioPlayerAction.PAUSE -> {
                audioPlayer.pause()
                stopForeground(STOP_FOREGROUND_REMOVE)
            }

            RadioPlayerAction.STOP -> {
                audioPlayer.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }

            else -> {}
        }

        return START_NOT_STICKY
    }

    private fun showForegroundNotification(titleText: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = buildNotification(titleText)
        startForeground(MEDIA_FOREGROUND_ID, notification)
    }

    override fun onDestroy() {
        serviceScope.cancel()
        audioPlayer.stop()
        audioPlayer.release()
        super.onDestroy()
    }

    private fun buildNotification(titleText: String): Notification {
        val stopIntent = Intent(this, RadioPlayerService::class.java).apply {
            putExtra(ACTION_SENDED, RadioPlayerAction.STOP.name)
        }

        val stopPendingIntent = PendingIntent.getService(
            this, 103, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("اذاعة القران الكريم")
            .setContentText(titleText)
            .setSmallIcon(R.drawable.ic_radio_selected)
            .setOngoing(true)
            .addAction(R.drawable.ic_stop, "إيقاف", stopPendingIntent)
            .setOnlyAlertOnce(true)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0)
            )
            .build()
    }
}
