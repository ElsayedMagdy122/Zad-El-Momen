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

class LocationRepositoryImpl(val context: Context, private val dataStore: DataStore<Preferences>,val settingsRepository: SettingsRepository) :
    LocationRepository {
    override suspend fun saveLocation(location: Location) {
        dataStore.edit { prefs ->
            prefs[SettingsKeys.LATITUDE_KEY] = location.latitude
            prefs[SettingsKeys.LONGITUDE_KEY] = location.longitude
        }
    }

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    override suspend fun getCountryAndState(): Pair<String, String> = withContext(Dispatchers.IO) {
        val location = settingsRepository.observeLocation().first()
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val country = addresses[0].countryName ?: "Unknown"
            val state = addresses[0].adminArea ?: "Unknown"
            country to state
        } else {
            "Unknown" to "Unknown"
        }
    }
    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location {
        return suspendCancellableCoroutine { cont ->
            fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener { location ->
                    if (location != null) {
                        cont.resume(
                            Location(
                                latitude = location.latitude,
                                longitude = location.longitude
                            )
                        )
                    } else {
                        cont.resumeWithException(
                            IllegalStateException("Location unavailable - GPS disabled")
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    cont.resumeWithException(exception)
                }
        }
    }

    override suspend fun getSavedLocation(): Location {
        val prefs = dataStore.data.first()
        val lat = prefs[SettingsKeys.LATITUDE_KEY] ?: 0.0
        val lon = prefs[SettingsKeys.LONGITUDE_KEY] ?: 0.0
        return Location(lat, lon)
    }
}