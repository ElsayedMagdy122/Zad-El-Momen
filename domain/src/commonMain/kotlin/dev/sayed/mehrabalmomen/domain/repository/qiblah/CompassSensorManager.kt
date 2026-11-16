package dev.sayed.mehrabalmomen.domain.repository.qiblah

import kotlinx.coroutines.flow.Flow

interface CompassSensorManager {
    fun observeAzimuth(): Flow<Float>
}
