package dev.sayed.mehrabalmomen.presentation.screen.qiblah

import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.repository.QiblahRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class QiblahViewModel(private val qiblahRepository: QiblahRepository) :
    BaseViewModel<QiblahUiState, QiblahEffect>(QiblahUiState()) {
    var fixedQiblaDirection: Float = Float.NaN
        private set

    init {
        getQiblahDirection()
    }

    private fun getQiblahDirection() {
        tryToCall(
            block = {
                qiblahRepository.getQiblahDirection(
                    location = Location(30.033333, 31.233334)
                )
            },
            onSuccess = { direction ->
                fixedQiblaDirection = direction.toFloat()
                updateState { it.copy(direction = 0f) }
            },
            onError = {}
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