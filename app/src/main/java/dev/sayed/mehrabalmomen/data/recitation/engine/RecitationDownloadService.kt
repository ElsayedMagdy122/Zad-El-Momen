package dev.sayed.mehrabalmomen.data.recitation.engine

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.presentation.base.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

/**
 * Foreground Service for robust, uninterrupted remote model downloading.
 * Continues downloading in background with live notification progress even if user minimizes app.
 */
class RecitationDownloadService : Service() {

    private val downloader: RecitationModelDownloader by inject()
    private var downloadJob: Job? = null

    companion object {
        private const val CHANNEL_ID = "recitation_download_channel"
        private const val NOTIFICATION_ID = 20261

        const val ACTION_START_DOWNLOAD = "ACTION_START_DOWNLOAD"
        const val ACTION_STOP_DOWNLOAD = "ACTION_STOP_DOWNLOAD"

        fun start(context: Context) {
            val intent = Intent(context, RecitationDownloadService::class.java).apply {
                action = ACTION_START_DOWNLOAD
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, RecitationDownloadService::class.java).apply {
                action = ACTION_STOP_DOWNLOAD
            }
            context.startService(intent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START_DOWNLOAD -> {
                val notification = buildNotification("جاري بدء تنزيل النموذج...", 0)
                startForeground(NOTIFICATION_ID, notification)
                observeProgressAndStart()
            }
            ACTION_STOP_DOWNLOAD -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun observeProgressAndStart() {
        downloadJob?.cancel()
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        downloadJob = scope.launch {
            launch {
                downloader.downloadState.collectLatest { state ->
                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    when (state) {
                        is ModelDownloadState.Downloading -> {
                            val percent = (state.progress * 100).toInt()
                            val notification = buildNotification("جاري تنزيل نموذج التعرف الصوتي... ($percent%)", percent)
                            notificationManager.notify(NOTIFICATION_ID, notification)
                        }
                        is ModelDownloadState.Ready -> {
                            stopForeground(STOP_FOREGROUND_REMOVE)
                            stopSelf()
                        }
                        is ModelDownloadState.Error -> {
                            stopForeground(STOP_FOREGROUND_REMOVE)
                            stopSelf()
                        }
                        else -> {}
                    }
                }
            }

            downloader.downloadAndPrepareModels()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "تنزيل نموذج التسميع",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "إشعارات تقدم تنزيل نموذج التعرف الصوتي للتسميع"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(contentText: String, progress: Int): android.app.Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("تنسيق نماذج التسميع بالذكاء الاصطناعي")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_download_01)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, progress, progress == 0)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadJob?.cancel()
    }
}
