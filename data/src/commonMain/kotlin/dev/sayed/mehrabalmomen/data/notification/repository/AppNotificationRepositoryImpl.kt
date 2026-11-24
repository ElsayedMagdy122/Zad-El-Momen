package dev.sayed.mehrabalmomen.data.notification.repository

import dev.sayed.mehrabalmomen.data.notification.local.dao.AppNotificationDao
import dev.sayed.mehrabalmomen.data.notification.local.entity.toDomain
import dev.sayed.mehrabalmomen.data.notification.local.entity.toEntity
import dev.sayed.mehrabalmomen.data.notification.remote.RemoteNotificationDto
import dev.sayed.mehrabalmomen.data.notification.remote.toEntity
import dev.sayed.mehrabalmomen.domain.model.notification.AppNotification
import dev.sayed.mehrabalmomen.domain.repository.notification.AppNotificationRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppNotificationRepositoryImpl(
    private val dao: AppNotificationDao,
    private val supabaseClient: SupabaseClient
) : AppNotificationRepository {

    override fun observeNotifications(): Flow<List<AppNotification>> {
        return dao.observeAllNotifications().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getUnreadCount(): Flow<Int> {
        return dao.getUnreadNotificationsCount()
    }

    override suspend fun markAsRead(id: String) {
        dao.markAsRead(id)
    }

    override suspend fun markAllAsRead() {
        dao.markAllAsRead()
    }

    override suspend fun deleteNotification(id: String) {
        dao.deleteNotification(id)
    }

    override suspend fun clearAll() {
        dao.clearAll()
    }

    override suspend fun insertNotification(notification: AppNotification) {
        dao.insertNotification(notification.toEntity())
    }

    override suspend fun syncRemoteNotifications() {
        try {
            val remoteNotifications = supabaseClient.postgrest["notifications"]
                .select()
                .decodeList<RemoteNotificationDto>()
            val entities = remoteNotifications.map { it.toEntity() }
            if (entities.isNotEmpty()) {
                dao.insertNotifications(entities)
            }
        } catch (_: Throwable) {
            // Ignore offline network errors gracefully
        }
    }
}
