package dev.sayed.mehrabalmomen.domain.repository.qiblah

import dev.sayed.mehrabalmomen.domain.entity.location.Location
import kotlinx.coroutines.flow.Flow

interface QiblahRepository {
    suspend fun getQiblahDirection(location: Location): Double
    fun observeCompassAzimuth(): Flow<Float>
}
