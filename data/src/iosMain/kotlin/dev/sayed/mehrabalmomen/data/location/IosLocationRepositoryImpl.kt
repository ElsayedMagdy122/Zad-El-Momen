package dev.sayed.mehrabalmomen.data.location

import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import kotlinx.coroutines.flow.first

class IosLocationRepositoryImpl(
    private val settingsRepository: SettingsRepository
) : LocationRepository {

    override suspend fun getLocation(): Location {
        val saved = settingsRepository.observeLocation().first()
        if (saved.isValid()) return saved

        val (lat, lng) = IosLocationService.currentCoordinates()
        val placemark = runCatching { IosLocationService.reverseGeocode(lat, lng) }.getOrNull()

        val final = Location(
            latitude = lat,
            longitude = lng,
            country = placemark?.country ?: "Unknown",
            state = placemark?.administrativeArea ?: "Unknown"
        )
        settingsRepository.saveLocation(final)
        return final
    }

    override suspend fun getLocation(lat: Double, lng: Double): Location {
        val placemark = runCatching { IosLocationService.reverseGeocode(lat, lng) }.getOrNull()
        return Location(
            latitude = lat,
            longitude = lng,
            country = "${placemark?.administrativeArea ?: "Unknown"}, ${placemark?.country ?: "Unknown"}",
            state = placemark?.thoroughfare ?: placemark?.name ?: ""
        )
    }

    override suspend fun getCurrentDeviceLocation(): Location {
        val (lat, lng) = IosLocationService.currentCoordinates()
        return Location(latitude = lat, longitude = lng)
    }

    private fun Location.isValid(): Boolean =
        latitude != 0.0 && longitude != 0.0 && country != "Unknown" && state != "Unknown"
}
