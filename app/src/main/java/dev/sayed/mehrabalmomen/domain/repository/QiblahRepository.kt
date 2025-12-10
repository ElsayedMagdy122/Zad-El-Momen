package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.entity.Location

interface QiblahRepository {
    suspend fun getQiblahDirection(location: Location): Double
}