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

/**
 * Foreground Service for robust, uninterrupted remote model downloading.
 * Continues downloading in background with live notification progress even if user minimizes app or navigates away.
 */
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
                    contentText = "تنزيل نموذج مراجعة الحفظ",
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
                            val text = "يتم الآن تحميل ملفات الذكاء الاصطناعي، يرجى الانتظار... ($percent%)"
                            val notification = buildNotification(
                                contentText = text,
                                progress = percent,
                                isIndeterminate = false
                            )
                            notificationManager.notify(NOTIFICATION_ID, notification)
                        }
                        is ModelDownloadState.Processing -> {
                            val text = state.message.ifBlank {
                                "جاري فك ضغط وتجهيز نماذج مراجعة الحفظ..."
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
                                contentText = "تم تثبيت نماذج مراجعة الحفظ بنجاح",
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
                "تنزيل نموذج مراجعة الحفظ",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "إشعارات تقدم تنزيل نموذج التعرف الصوتي لمراجعة الحفظ"
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
        val openAppIntent = try {
            val mainActivityClass = Class.forName("dev.sayed.mehrabalmomen.presentation.base.MainActivity")
            Intent(this, mainActivityClass).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        } catch (_: Exception) {
            packageManager.getLaunchIntentForPackage(packageName)
        }

        val pendingIntent = if (openAppIntent != null) {
            PendingIntent.getActivity(
                this,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } else null

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("تنزيل نموذج مراجعة الحفظ الصوتي")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, progress, isIndeterminate)

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent)
        }

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
                "إلغاء التنزيل",
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