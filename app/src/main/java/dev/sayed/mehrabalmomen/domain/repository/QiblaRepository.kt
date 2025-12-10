package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.entity.Location

interface QiblaRepository {
    suspend fun getQiblaDirection(location: Location): Double
}