package dev.sayed.mehrabalmomen.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.maplibre.android.geometry.LatLng
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationRepositoryImpl(val context: Context, val settingsRepository: SettingsRepository) :
    LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    suspend fun getCurrentOrSavedLocation(): Location {
        val savedLocation = settingsRepository.observeLocation().first()
        if (savedLocation.isValid()) return savedLocation

        val location = suspendCancellableCoroutine<Location> { cont ->
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { loc ->
                    if (loc != null) cont.resume(
                        Location(
                            latitude = loc.latitude,
                            longitude = loc.longitude,
                        )
                    ) else cont.resumeWithException(IllegalStateException("Location unavailable"))
                }
                .addOnFailureListener(cont::resumeWithException)
        }

        val (country, state) = withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, appLocale(context))
            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
            if (!addresses.isNullOrEmpty()) {
                val addr = addresses[0]
                (addr.countryName ?: "Unknown") to (addr.adminArea ?: "Unknown")
            } else "Unknown" to "Unknown"
        }

        val finalLocation = location.copy(country = country, state = state)
        settingsRepository.saveLocation(finalLocation)

        return finalLocation
    }

    private fun Location.isValid(): Boolean =
        latitude != 0.0 && longitude != 0.0 && country != "Unknown" && state != "Unknown"


    override suspend fun getLocation(): Location {
        return getCurrentOrSavedLocation()
    }

    override suspend fun getLocation(lat: Double, lng: Double): Location =
        withContext(Dispatchers.IO) {
            val geocoder = Geocoder(context, appLocale(context))
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            val addr = addresses?.firstOrNull()

            Location(
                latitude = lat,
                longitude = lng,
                country = "${addr?.adminArea ?: "Unknown"}, ${addr?.countryName ?: "Unknown"}",
                state = addr?.thoroughfare ?: addr?.featureName ?: ""
            )
        }

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentDeviceLocation(): LatLng =
        suspendCancellableCoroutine { cont ->
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                null
            ).addOnSuccessListener { location ->
                if (location != null) {
                    cont.resume(LatLng(location.latitude, location.longitude))
                } else {
                    fusedLocationClient.lastLocation
                        .addOnSuccessListener { fallback ->
                            if (fallback != null) {
                                cont.resume(LatLng(fallback.latitude, fallback.longitude))
                            } else {
                                cont.resumeWithException(
                                    IllegalStateException("Location unavailable")
                                )
                            }
                        }
                }
            }.addOnFailureListener(cont::resumeWithException)
        }
}

private fun appLocale(context: Context): Locale {
    return context.resources.configuration.locales[0]
}