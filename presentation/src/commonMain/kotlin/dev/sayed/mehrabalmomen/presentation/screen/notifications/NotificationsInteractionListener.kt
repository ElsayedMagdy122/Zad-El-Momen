package dev.sayed.mehrabalmomen.presentation.screen.notifications

import dev.sayed.mehrabalmomen.domain.model.notification.AppNotification

interface NotificationsInteractionListener {
    fun onNotificationClick(notification: AppNotification)
    fun onMarkAllAsReadClick()
    fun onDeleteNotificationClick(id: String)
    fun onClearAllClick()
    fun onFilterSelected(filter: NotificationFilter)
    fun onBackClick()
}
