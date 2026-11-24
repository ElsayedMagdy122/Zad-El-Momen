package dev.sayed.mehrabalmomen.domain.model.notification

import kotlinx.serialization.Serializable

@Serializable
data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val targetId: String? = null,
    val deepLink: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long
)
