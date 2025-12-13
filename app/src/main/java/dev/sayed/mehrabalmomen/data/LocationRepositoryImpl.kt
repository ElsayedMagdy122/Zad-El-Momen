package dev.sayed.mehrabalmomen.data

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.repository.LocationRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LocationRepositoryImpl(val context: Context, private val dataStore: DataStore<Preferences>) :
    LocationRepository {
    override suspend fun saveLocation(location: Location) {
        dataStore.edit { prefs ->
            prefs[DataStoreKeys.LATITUDE_KEY] = location.latitude
            prefs[DataStoreKeys.LONGITUDE_KEY] = location.longitude
        }
    }

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)


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
        val lat = prefs[DataStoreKeys.LATITUDE_KEY] ?: 0.0
        val lon = prefs[DataStoreKeys.LONGITUDE_KEY] ?: 0.0
        return Location(lat, lon)
    }
}