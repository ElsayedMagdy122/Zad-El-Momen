package dev.sayed.mehrabalmomen.data.notification.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.sayed.mehrabalmomen.domain.model.notification.AppNotification
import dev.sayed.mehrabalmomen.domain.model.notification.NotificationType

@Entity(tableName = "app_notifications")
data class AppNotificationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val body: String,
    val type: String,
    val targetId: String? = null,
    val deepLink: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long
)

fun AppNotificationEntity.toDomain(): AppNotification {
    return AppNotification(
        id = id,
        title = title,
        body = body,
        type = try {
            NotificationType.valueOf(type)
        } catch (_: Throwable) {
            NotificationType.GENERAL
        },
        targetId = targetId,
        deepLink = deepLink,
        isRead = isRead,
        createdAt = createdAt
    )
}

fun AppNotification.toEntity(): AppNotificationEntity {
    return AppNotificationEntity(
        id = id,
        title = title,
        body = body,
        type = type.name,
        targetId = targetId,
        deepLink = deepLink,
        isRead = isRead,
        createdAt = createdAt
    )
}
