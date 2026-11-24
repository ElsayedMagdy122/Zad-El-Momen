package dev.sayed.mehrabalmomen.data.notification.remote

import dev.sayed.mehrabalmomen.data.notification.local.entity.AppNotificationEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RemoteNotificationDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("body")
    val body: String,
    @SerialName("type")
    val type: String,
    @SerialName("target_id")
    val targetId: String? = null,
    @SerialName("deep_link")
    val deepLink: String? = null,
    @SerialName("created_at")
    val createdAt: Long? = null
)

fun RemoteNotificationDto.toEntity(): AppNotificationEntity {
    return AppNotificationEntity(
        id = id,
        title = title,
        body = body,
        type = type,
        targetId = targetId,
        deepLink = deepLink,
        isRead = false,
        createdAt = createdAt ?: 0L
    )
}
