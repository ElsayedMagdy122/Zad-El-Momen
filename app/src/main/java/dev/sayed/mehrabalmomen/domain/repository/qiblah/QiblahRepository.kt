package dev.sayed.mehrabalmomen.domain.repository.qiblah

import dev.sayed.mehrabalmomen.domain.entity.location.Location

interface QiblahRepository {
    suspend fun getQiblahDirection(location: Location): Double
}