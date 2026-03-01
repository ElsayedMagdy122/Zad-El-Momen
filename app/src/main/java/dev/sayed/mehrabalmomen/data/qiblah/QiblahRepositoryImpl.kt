package dev.sayed.mehrabalmomen.data.qiblah

import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.Qibla
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.repository.qiblah.QiblahRepository

class QiblahRepositoryImpl: QiblahRepository {
    override suspend fun getQiblahDirection(location: Location): Double {
        val coordinates = Coordinates(location.latitude, location.longitude)
        val qiblaDirection = Qibla(coordinates).direction
        return qiblaDirection
    }
}