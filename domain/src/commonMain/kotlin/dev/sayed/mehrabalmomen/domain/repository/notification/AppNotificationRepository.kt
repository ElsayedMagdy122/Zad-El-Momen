package dev.sayed.mehrabalmomen.domain.repository.notification

import dev.sayed.mehrabalmomen.domain.model.notification.AppNotification
import kotlinx.coroutines.flow.Flow

interface AppNotificationRepository {
    fun observeNotifications(): Flow<List<AppNotification>>
    fun getUnreadCount(): Flow<Int>
    suspend fun markAsRead(id: String)
    suspend fun markAllAsRead()
    suspend fun deleteNotification(id: String)
    suspend fun clearAll()
    suspend fun insertNotification(notification: AppNotification)
    suspend fun syncRemoteNotifications()
}
