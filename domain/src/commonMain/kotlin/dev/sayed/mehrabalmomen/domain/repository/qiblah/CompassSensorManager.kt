package dev.sayed.mehrabalmomen.domain.repository.qiblah

import dev.sayed.mehrabalmomen.domain.entity.location.Location
import kotlinx.coroutines.flow.Flow

data class CompassData(
    val azimuth: Float,
    val accuracy: Int = SENSOR_ACCURACY_HIGH
) {
    companion object {
        const val SENSOR_ACCURACY_UNRELIABLE = 0
        const val SENSOR_ACCURACY_LOW = 1
        const val SENSOR_ACCURACY_MEDIUM = 2
        const val SENSOR_ACCURACY_HIGH = 3
    }

    val isLowAccuracy: Boolean get() = accuracy <= SENSOR_ACCURACY_LOW
}

interface CompassSensorManager {
    fun observeAzimuth(location: Location? = null): Flow<CompassData>
}
