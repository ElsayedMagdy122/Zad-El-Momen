package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.entity.Location

interface LocationRepository {
    suspend fun getOrDetectLocation(): Location
}