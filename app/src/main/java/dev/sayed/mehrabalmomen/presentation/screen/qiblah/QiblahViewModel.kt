package dev.sayed.mehrabalmomen.presentation.screen.qiblah

import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.repository.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.QiblahRepository
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.first

class QiblahViewModel(
    private val qiblahRepository: QiblahRepository,
    private val settingsRepository: SettingsRepository,
    private val locationRepository: LocationRepository
) :
    BaseViewModel<QiblahUiState, QiblahEffect>(QiblahUiState()) {
    var fixedQiblaDirection: Float = Float.NaN
        private set

    init {
        getQiblahDirection()
        getLocation()
    }

    private fun getQiblahDirection() {
        tryToCall(
            block = {
                val location = settingsRepository.observeLocation().first()
                qiblahRepository.getQiblahDirection(
                    location = Location(
                        latitude = location.latitude,
                        longitude = location.longitude
                    )
                )
            },
            onSuccess = { direction ->
                fixedQiblaDirection = direction.toFloat()
                updateState { it.copy(direction = 0f) }
            },
            onError = {}
        )
    }

    private fun getLocation() {
        tryToCall(
            block = {
                val location = locationRepository.getCountryAndState()
                location
            },
            onSuccess = {
                val location = QiblahUiState.LocationUiState(
                    country = it.first,
                    city = it.second
                )
                updateState { state ->
                    state.copy(
                        location = location
                    )
                }
            },
            onError = {
                val location = QiblahUiState.LocationUiState(country = "Unknown", city = "Unknown")
                updateState { it.copy(location = location) }
            }
        )
    }

    fun updateDirection(deviceAzimuth: Float) {
        if (fixedQiblaDirection.isNaN()) return
        var relative = fixedQiblaDirection - deviceAzimuth
        if (relative > 180f) relative -= 360f
        if (relative < -180f) relative += 360f
        updateState { it.copy(direction = relative) }
    }
}