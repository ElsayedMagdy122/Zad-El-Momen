package dev.sayed.mehrabalmomen.domain.repository.location

import dev.sayed.mehrabalmomen.domain.entity.location.Location

interface LocationRepository {
    suspend fun getLocation(): Location
    suspend fun getLocation(lat: Double, lng: Double): Location
    suspend fun getCurrentDeviceLocation(): Location
}
