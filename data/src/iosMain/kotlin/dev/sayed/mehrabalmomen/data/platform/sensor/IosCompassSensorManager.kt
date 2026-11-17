package dev.sayed.mehrabalmomen.data.platform.sensor

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
    var onHeading: ((Float) -> Unit)? = null

    override fun locationManager(manager: CLLocationManager, didUpdateHeading: CLHeading) {
        val heading = didUpdateHeading.trueHeading.toFloat().takeIf { it >= 0f } ?: didUpdateHeading.magneticHeading.toFloat()
        onHeading?.invoke(heading)
    }

    override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {}
}

@OptIn(ExperimentalForeignApi::class)
class IosCompassSensorManager : CompassSensorManager {
    private val delegate = HeadingDelegate()
    private val manager = CLLocationManager().apply {
        delegate = this@IosCompassSensorManager.delegate
    }

    override fun observeAzimuth(): Flow<Float> = callbackFlow {
        delegate.onHeading = { heading ->
            trySend(heading)
        }
        manager.startUpdatingHeading()
        awaitClose {
            manager.stopUpdatingHeading()
            delegate.onHeading = null
        }
    }
}
