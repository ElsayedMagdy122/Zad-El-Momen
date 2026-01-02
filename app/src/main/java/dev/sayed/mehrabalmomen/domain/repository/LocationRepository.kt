package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.entity.Location
import org.maplibre.android.geometry.LatLng

interface LocationRepository {
    suspend fun getLocation(): Location
    suspend fun getLocation(lat: Double, lng: Double): Location
    suspend fun getCurrentDeviceLocation(): LatLng
}