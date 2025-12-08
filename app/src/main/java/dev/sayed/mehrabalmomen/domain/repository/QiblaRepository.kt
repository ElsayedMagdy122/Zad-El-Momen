package dev.sayed.mehrabalmomen.domain.repository

interface QiblaRepository {
    suspend fun getQiblaDirection(): Double
}