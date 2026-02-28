package dev.sayed.mehrabalmomen.domain.repository.prayer

import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import kotlinx.coroutines.flow.Flow

interface PrayerNotificationsRepository {
    suspend fun setPrayerEnabled(
        prayer: Prayer.PrayerName,
        enabled: Boolean
    )

    fun observePrayerEnabled(
        prayer: Prayer.PrayerName
    ): Flow<Boolean>

    fun observeAll(): Flow<Map<Prayer.PrayerName, Boolean>>
}