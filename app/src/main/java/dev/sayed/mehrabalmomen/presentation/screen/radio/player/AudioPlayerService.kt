package dev.sayed.mehrabalmomen.presentation.screen.radio.player

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerConstants.ACTION_SENDED
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerConstants.CHANNEL_ID
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerConstants.CHANNEL_NAME
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerConstants.MEDIA_FOREGROUND_ID
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerConstants.NOTIFICATION_TITLE
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerConstants.STREAM_URL
import org.koin.android.ext.android.inject

class AudioPlayerService : Service() {

    private val playerController: PlayerController by inject()

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val actionString = intent?.getStringExtra(ACTION_SENDED)
        val url = intent?.getStringExtra(STREAM_URL)
        val action = actionString?.let { AudioPlayerAction.valueOf(it) }
        val titleText = intent?.getStringExtra(NOTIFICATION_TITLE) ?: "Quran Player"

        when (action) {
            AudioPlayerAction.PLAY -> url?.let {
                playerController.play(it)
                updateForegroundNotification( titleText)
            }

            AudioPlayerAction.PAUSE -> {
                playerController.pause()
                stopForeground(STOP_FOREGROUND_REMOVE)
            }

            AudioPlayerAction.STOP -> {
                playerController.stop()
                stopForeground(STOP_FOREGROUND_REMOVE)
            }

            else -> {}
        }

        playerController.setOnErrorListener { e ->
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun updateForegroundNotification(titleText: String) {
        val notification = buildNotification(titleText)
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        startForeground(MEDIA_FOREGROUND_ID, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        playerController.stop()
        playerController.release()
    }

    private fun buildNotification(
        titleText: String
    ): Notification {
        val channelId = CHANNEL_ID
        val artworkBitmap: Bitmap? = BitmapFactory.decodeResource(
            resources,
            R.drawable.image_mosque_dark
        )

        val stopIntent = Intent(this, AudioPlayerService::class.java).apply {
            putExtra(ACTION_SENDED, AudioPlayerAction.STOP.name)
        }

        val stopPendingIntent = PendingIntent.getService(
            this, 103, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("اذاعة القران الكريم")
            .setContentText(titleText)
            .setSmallIcon(R.drawable.ic_radio_selected)
            .setLargeIcon(artworkBitmap)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_stop,
                "إيقاف",
                stopPendingIntent
            )
            .setOnlyAlertOnce(true)
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0)
            )

        return builder.build()
    }

}