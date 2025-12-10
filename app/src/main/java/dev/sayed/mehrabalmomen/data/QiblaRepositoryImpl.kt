package dev.sayed.mehrabalmomen.data

import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.Qibla
import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.repository.QiblaRepository

class QiblaRepositoryImpl: QiblaRepository {
    override suspend fun getQiblaDirection(location: Location): Double {
        val coordinates = Coordinates(location.latitude, location.longitude)
        val qiblaDirection = Qibla(coordinates).direction
        return qiblaDirection
    }
}