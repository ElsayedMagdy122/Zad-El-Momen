package dev.sayed.mehrabalmomen.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dev.sayed.mehrabalmomen.domain.model.notification.AppNotification
import dev.sayed.mehrabalmomen.domain.model.notification.NotificationType
import dev.sayed.mehrabalmomen.domain.repository.notification.AppNotificationRepository
import dev.sayed.mehrabalmomen.presentation.base.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class AppFirebaseMessagingService : FirebaseMessagingService() {

    private val repository: AppNotificationRepository by inject()
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        private var lastSoundTimestamp = 0L
        private const val SOUND_THROTTLE_INTERVAL_MS = 15_000L // 15 seconds threshold
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val data = remoteMessage.data
        val title = data["title"] ?: remoteMessage.notification?.title ?: "محراب المؤمن"
        val body = data["body"] ?: remoteMessage.notification?.body ?: ""
        val typeStr = data["type"] ?: "GENERAL"
        val targetId = data["target_id"]
        val deepLink = data["deep_link"]
        val notificationId = data["id"] ?: System.currentTimeMillis().toString()

        val type = try {
            NotificationType.valueOf(typeStr)
        } catch (_: Throwable) {
            NotificationType.GENERAL
        }

        val appNotification = AppNotification(
            id = notificationId,
            title = title,
            body = body,
            type = type,
            targetId = targetId,
            deepLink = deepLink,
            isRead = false,
            createdAt = System.currentTimeMillis()
        )

        serviceScope.launch {
            try {
                repository.insertNotification(appNotification)
            } catch (_: Throwable) {
                // Ignore DB error
            }
        }

        showSystemNotification(notificationId.hashCode(), title, body, type, targetId, deepLink)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun showSystemNotification(
        notificationId: Int,
        title: String,
        body: String,
        type: NotificationType,
        targetId: String?,
        deepLink: String?
    ) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "app_notifications_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "إشعارات الباك إند والتطبيق",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات القراء والراديو وتحديثات التطبيق"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra("notification_type", type.name)
            putExtra("target_id", targetId)
            putExtra("deep_link", deepLink)
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Smart Sound Throttling: Prevent notification sound/vibration spam if multiple notifications arrive quickly (< 15 seconds)
        val currentTime = System.currentTimeMillis()
        val shouldPlaySound = (currentTime - lastSoundTimestamp) > SOUND_THROTTLE_INTERVAL_MS
        if (shouldPlaySound) {
            lastSoundTimestamp = currentTime
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)

        if (!shouldPlaySound) {
            // Silence rapid consecutive notifications so they don't spam the user
            notificationBuilder.setSilent(true)
        }

        val notification = notificationBuilder.build()
        notificationManager.notify(notificationId, notification)
    }
}
