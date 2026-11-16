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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class RecitationDownloadService : Service() {

    private val downloader: RecitationModelDownloader by inject()
    private var downloadJob: Job? = null
    private var serviceScope: CoroutineScope? = null

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
                if (downloadJob?.isActive == true) {
                    return START_NOT_STICKY
                }
                val notification = buildNotification(
                    contentText = "جاري تنزيل ملفات التسميع...",
                    progress = 0,
                    isIndeterminate = true
                )
                startForeground(NOTIFICATION_ID, notification)
                observeProgressAndStart()
            }
            ACTION_STOP_DOWNLOAD -> {
                downloadJob?.cancel()
                serviceScope?.cancel()
                downloader.resetState()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
        return START_NOT_STICKY
    }

    private fun observeProgressAndStart() {
        downloadJob?.cancel()
        serviceScope?.cancel()
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        serviceScope = scope
        downloadJob = scope.launch {
            launch {
                downloader.downloadState.collectLatest { state ->
                    val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    when (state) {
                        is ModelDownloadState.Downloading -> {
                            val percent = (state.progress * 100).toInt()
                            val text = "جاري التنزيل: $percent%"
                            val notification = buildNotification(
                                contentText = text,
                                progress = percent,
                                isIndeterminate = false
                            )
                            notificationManager.notify(NOTIFICATION_ID, notification)
                        }
                        is ModelDownloadState.Processing -> {
                            val text = state.message.ifBlank {
                                "جاري التجهيز والتحقق من الملفات..."
                            }
                            val notification = buildNotification(
                                contentText = text,
                                progress = 0,
                                isIndeterminate = true
                            )
                            notificationManager.notify(NOTIFICATION_ID, notification)
                        }
                        is ModelDownloadState.Ready -> {
                            val notification = buildNotification(
                                contentText = "تم تنزيل وتجهيز نماذج التسميع بنجاح",
                                progress = 100,
                                isIndeterminate = false,
                                showCancelAction = false
                            )
                            notificationManager.notify(NOTIFICATION_ID, notification)
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
                "تنزيل نماذج التسميع",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "إشعارات تنزيل نماذج التعرف الصوتي والتسميع"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(
        contentText: String,
        progress: Int,
        isIndeterminate: Boolean = false,
        showCancelAction: Boolean = true
    ): android.app.Notification {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val iconResId = resources.getIdentifier("ic_download_01", "drawable", packageName)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("تنزيل نماذج الذكاء الاصطناعي للتسميع")
            .setContentText(contentText)
            .setSmallIcon(if (iconResId != 0) iconResId else android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, progress, isIndeterminate)
            .setContentIntent(pendingIntent)

        if (showCancelAction) {
            val stopIntent = Intent(this, RecitationDownloadService::class.java).apply {
                action = ACTION_STOP_DOWNLOAD
            }
            val stopPendingIntent = PendingIntent.getService(
                this,
                1,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "إلغاء",
                stopPendingIntent
            )
        }

        return builder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        downloadJob?.cancel()
        serviceScope?.cancel()
    }
}
