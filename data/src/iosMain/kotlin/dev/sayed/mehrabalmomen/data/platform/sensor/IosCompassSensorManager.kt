package dev.sayed.mehrabalmomen.data.platform.sensor

import dev.sayed.mehrabalmomen.domain.repository.qiblah.CompassSensorManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class IosCompassSensorManager : CompassSensorManager {
    override fun observeAzimuth(): Flow<Float> {
        return flowOf(0f)
    }
}
