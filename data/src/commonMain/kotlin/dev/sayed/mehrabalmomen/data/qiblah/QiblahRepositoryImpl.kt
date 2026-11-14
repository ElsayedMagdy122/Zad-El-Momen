package dev.sayed.mehrabalmomen.data.qiblah

import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.Qibla
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import dev.sayed.mehrabalmomen.domain.repository.qiblah.QiblahRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class QiblahRepositoryImpl : QiblahRepository {

    override suspend fun getQiblahDirection(location: Location): Double {
        val coordinates = Coordinates(location.latitude, location.longitude)
        return Qibla(coordinates).direction
    }

    override fun observeCompassAzimuth(): Flow<Float> {
        return flowOf(0f)
    }
}
