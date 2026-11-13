package dev.sayed.mehrabalmomen.data.qiblah

import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.Qibla
import dev.sayed.mehrabalmomen.data.platform.sensor.CompassSensorManager
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.repository.qiblah.QiblahRepository
import kotlinx.coroutines.flow.Flow

class QiblahRepositoryImpl(
    private val compassSensorManager: CompassSensorManager
) : QiblahRepository {

    override suspend fun getQiblahDirection(location: Location): Double {
        val coordinates = Coordinates(location.latitude, location.longitude)
        return Qibla(coordinates).direction
    }

    override fun observeCompassAzimuth(): Flow<Float> {
        return compassSensorManager.observeAzimuth()
    }
}
