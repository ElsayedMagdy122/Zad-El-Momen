package dev.sayed.mehrabalmomen.presentation.screen.notifications

import dev.sayed.mehrabalmomen.domain.model.notification.AppNotification

enum class NotificationFilter {
    ALL,
    UNREAD
}

data class NotificationsUiState(
    val notifications: List<AppNotification> = emptyList(),
    val filteredNotifications: List<AppNotification> = emptyList(),
    val filter: NotificationFilter = NotificationFilter.ALL,
    val unreadCount: Int = 0,
    val isLoading: Boolean = false,
    val error: String? = null
)
