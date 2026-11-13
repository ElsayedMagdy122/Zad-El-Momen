package dev.sayed.mehrabalmomen.data.platform.sensor

import kotlinx.coroutines.flow.Flow

interface CompassSensorManager {
    fun observeAzimuth(): Flow<Float>
}
