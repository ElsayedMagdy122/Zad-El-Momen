package dev.sayed.mehrabalmomen.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dev.sayed.mehrabalmomen.data.local.SettingsKeys
import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.repository.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationRepositoryImpl(val context: Context,val settingsRepository: SettingsRepository) :
    LocationRepository {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
//
//     suspend fun getCountryAndState(): Pair<String, String> = withContext(Dispatchers.IO) {
//        val location = settingsRepository.observeLocation().first()
//        val geocoder = Geocoder(context, Locale.getDefault())
//        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
//        if (!addresses.isNullOrEmpty()) {
//            val country = addresses[0].countryName ?: "Unknown"
//            val state = addresses[0].adminArea ?: "Unknown"
//            country to state
//        } else {
//            "Unknown" to "Unknown"
//        }
//    }
//    @SuppressLint("MissingPermission")
//     suspend fun getCurrentLocation(): Location {
//        return suspendCancellableCoroutine { cont ->
//            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
//                .addOnSuccessListener { location ->
//                    if (location != null) {
//                        cont.resume(
//                            Location(
//                                latitude = location.latitude,
//                                longitude = location.longitude
//                            )
//                        )
//                    } else {
//                        cont.resumeWithException(
//                            IllegalStateException("Location unavailable - GPS disabled")
//                        )
//                    }
//                }
//                .addOnFailureListener { exception ->
//                    cont.resumeWithException(exception)
//                }
//        }
//    }

    override suspend fun getOrDetectLocation(): Location {

        // 1️⃣ اقرأ من SettingsRepository
        val savedLocation = settingsRepository.observeLocation().first()

        if (savedLocation.isValid()) {
            return savedLocation
        }

        val currentLocation = getCurrentLocation()

        val (country, state) = getCountryAndState(
            currentLocation.latitude,
            currentLocation.longitude
        )

        val finalLocation = currentLocation.copy(
            country = country,
            state = state
        )
        settingsRepository.saveLocation(finalLocation)

        return finalLocation
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Location =
        suspendCancellableCoroutine { cont ->
            fusedLocationClient
                .getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        cont.resume(
                            Location(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                country = "Unknown",
                                state = "Unknown"
                            )
                        )
                    } else {
                        cont.resumeWithException(
                            IllegalStateException("Location unavailable")
                        )
                    }
                }
                .addOnFailureListener(cont::resumeWithException)
        }

    private suspend fun getCountryAndState(
        lat: Double,
        lon: Double
    ): Pair<String, String> = withContext(Dispatchers.IO) {

        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lon, 1)

        if (!addresses.isNullOrEmpty()) {
            val addr = addresses[0]
            (addr.countryName ?: "Unknown") to
                    (addr.adminArea ?: "Unknown")
        } else {
            "Unknown" to "Unknown"
        }
    }
    private fun Location.isValid(): Boolean {
        return latitude != 0.0 &&
                longitude != 0.0 &&
                country != "Unknown" &&
                state != "Unknown"
    }
}