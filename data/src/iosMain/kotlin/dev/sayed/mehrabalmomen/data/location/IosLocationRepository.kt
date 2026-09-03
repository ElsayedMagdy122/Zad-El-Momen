package dev.sayed.mehrabalmomen.data.location

import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository

class IosLocationRepository : LocationRepository {
    override suspend fun getLocation(): Location {
        return Location(0.0, 0.0, "Unknown", "Unknown")
    }

    override suspend fun getLocation(lat: Double, lng: Double): Location {
        return Location(lat, lng, "Unknown", "Unknown")
    }

    override suspend fun getCurrentDeviceLocation(): Location {
        return Location(0.0, 0.0, "Unknown", "Unknown")
    }
}
