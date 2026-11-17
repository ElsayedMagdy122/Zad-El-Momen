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

class RadioPlayerService : Service() {

    private val audioPlayer: AudioPlayer by inject(named("radio"))
    private val serviceScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val actionString = intent?.getStringExtra(ACTION_SENDED)
        val url = intent?.getStringExtra(STREAM_URL)
        val action = actionString?.let { runCatching { RadioPlayerAction.valueOf(it) }.getOrNull() }

        when (action) {
            RadioPlayerAction.PLAY -> {
                val title = intent?.getStringExtra(NOTIFICATION_TITLE) ?: "راديو القرآن الكريم"
                createNotificationChannel()
                startForeground(MEDIA_FOREGROUND_ID, buildNotification(title))

                if (!url.isNullOrEmpty()) {
                    serviceScope.launch {
                        audioPlayer.play(AudioSource.RemoteUrl(url))
                    }
                }
            }

            RadioPlayerAction.STOP -> {
                serviceScope.launch {
                    audioPlayer.stop()
                }
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }

            else -> {}
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
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
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "إيقاف", stopPendingIntent)
            .setOnlyAlertOnce(true)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0)
            )
            .build()
    }
}
