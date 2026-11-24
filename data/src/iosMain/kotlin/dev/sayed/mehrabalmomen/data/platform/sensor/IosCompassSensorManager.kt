package dev.sayed.mehrabalmomen.data.platform.sensor

import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.repository.qiblah.CompassData
import dev.sayed.mehrabalmomen.domain.repository.qiblah.CompassSensorManager
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreLocation.CLHeading
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSError
import platform.darwin.NSObject

private class HeadingDelegate : NSObject(), CLLocationManagerDelegateProtocol {
    var onHeading: ((CompassData) -> Unit)? = null

    override fun locationManager(manager: CLLocationManager, didUpdateHeading: CLHeading) {
        val heading = didUpdateHeading.trueHeading.toFloat().takeIf { it >= 0f } ?: didUpdateHeading.magneticHeading.toFloat()
        val accuracy = when {
            didUpdateHeading.headingAccuracy < 0 -> CompassData.SENSOR_ACCURACY_UNRELIABLE
            didUpdateHeading.headingAccuracy > 15.0 -> CompassData.SENSOR_ACCURACY_LOW
            didUpdateHeading.headingAccuracy > 10.0 -> CompassData.SENSOR_ACCURACY_MEDIUM
            else -> CompassData.SENSOR_ACCURACY_HIGH
        }
        onHeading?.invoke(CompassData(azimuth = heading, accuracy = accuracy))
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {}
}

@OptIn(ExperimentalForeignApi::class)
class IosCompassSensorManager : CompassSensorManager {
    private val delegate = HeadingDelegate()
    private val manager = CLLocationManager().apply {
        delegate = this@IosCompassSensorManager.delegate
    }

    override fun observeAzimuth(location: Location?): Flow<CompassData> = callbackFlow {
        delegate.onHeading = { data ->
            trySend(data)
        }
        manager.startUpdatingHeading()
        awaitClose {
            manager.stopUpdatingHeading()
            delegate.onHeading = null
        }
    }
}
