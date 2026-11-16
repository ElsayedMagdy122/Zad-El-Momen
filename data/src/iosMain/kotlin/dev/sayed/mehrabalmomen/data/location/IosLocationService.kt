package dev.sayed.mehrabalmomen.data.location

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import platform.CoreLocation.CLGeocoder
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.CLPlacemark
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.NSObject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

private class LocationDelegate : NSObject(), CLLocationManagerDelegateProtocol {
    var onLocation: ((CLLocation) -> Unit)? = null
    var onError: ((Throwable) -> Unit)? = null
    var onAuthChanged: (() -> Unit)? = null

    override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
        (didUpdateLocations.lastOrNull() as? CLLocation)?.let { onLocation?.invoke(it) }
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
        onError?.invoke(IllegalStateException(didFailWithError.localizedDescription))
    }

    override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
        onAuthChanged?.invoke()
    }
}

@OptIn(ExperimentalForeignApi::class)
object IosLocationService {

    private val delegate = LocationDelegate()
    private val manager = CLLocationManager().apply {
        this.delegate = this@IosLocationService.delegate
        desiredAccuracy = kCLLocationAccuracyBest
    }

    fun hasPermission(): Boolean {
        val s = CLLocationManager.authorizationStatus()
        return s == kCLAuthorizationStatusAuthorizedAlways ||
                s == kCLAuthorizationStatusAuthorizedWhenInUse
    }

    fun isLocationServiceEnabled(): Boolean = CLLocationManager.locationServicesEnabled()

    suspend fun requestPermission(): Boolean = withContext(Dispatchers.Main) {
        if (CLLocationManager.authorizationStatus() == kCLAuthorizationStatusNotDetermined) {
            suspendCancellableCoroutine<Unit> { cont ->
                delegate.onAuthChanged = {
                    if (CLLocationManager.authorizationStatus() != kCLAuthorizationStatusNotDetermined) {
                        delegate.onAuthChanged = null
                        if (cont.isActive) cont.resume(Unit)
                    }
                }
                cont.invokeOnCancellation { delegate.onAuthChanged = null }
                manager.requestWhenInUseAuthorization()
            }
        }
        hasPermission()
    }

    suspend fun requestNotificationPermission(): Boolean =
        suspendCancellableCoroutine { cont ->
            UNUserNotificationCenter.currentNotificationCenter()
                .requestAuthorizationWithOptions(
                    UNAuthorizationOptionAlert or UNAuthorizationOptionSound or UNAuthorizationOptionBadge
                ) { granted, _ -> if (cont.isActive) cont.resume(granted) }
        }

    /** (lat, lng) */
    suspend fun currentCoordinates(): Pair<Double, Double> = withContext(Dispatchers.Main) {
        if (!requestPermission()) throw IllegalStateException("Location permission denied")

        try {
            suspendCancellableCoroutine { cont ->
                delegate.onLocation = { loc ->
                    delegate.onLocation = null
                    delegate.onError = null
                    val result = loc.coordinate.useContents { latitude to longitude }
                    if (cont.isActive) cont.resume(result)
                }
                delegate.onError = { e ->
                    delegate.onLocation = null
                    delegate.onError = null
                    if (cont.isActive) cont.resumeWithException(e)
                }
                cont.invokeOnCancellation {
                    delegate.onLocation = null
                    delegate.onError = null
                }
                manager.requestLocation()
            }
        } catch (e: Exception) {
            val last = manager.location
                ?: throw IllegalStateException("Location unavailable")
            last.coordinate.useContents { latitude to longitude }
        }
    }

    suspend fun reverseGeocode(lat: Double, lng: Double): CLPlacemark? =
        withContext(Dispatchers.Main) {
            suspendCancellableCoroutine { cont ->
                val geocoder = CLGeocoder()
                cont.invokeOnCancellation { geocoder.cancelGeocode() }
                geocoder.reverseGeocodeLocation(
                    CLLocation(latitude = lat, longitude = lng)
                ) { placemarks, _ ->
                    if (cont.isActive) cont.resume(placemarks?.firstOrNull() as? CLPlacemark)
                }
            }
        }
}
