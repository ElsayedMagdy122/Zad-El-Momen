package dev.sayed.mehrabalmomen.presentation.screen.qiblah

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.qiblah.QiblahRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class QiblahViewModel(
    private val qiblahRepository: QiblahRepository,
    private val locationRepository: LocationRepository,
    private val analyticsTracker: AnalyticsTracker
) : BaseViewModel<QiblahUiState, QiblahEffect>(QiblahUiState()) {

    init {
        getQiblahDirection()
    }

    fun onScreenOpened() {
        analyticsTracker.logScreen("qiblah")
    }

    private fun getQiblahDirection() {
        viewModelScope.launch {
            try {
                val location = locationRepository.getLocation()
                val direction = qiblahRepository.getQiblahDirection(location)
                
                updateState {
                    it.copy(
                        direction = direction.toFloat(),
                        location = QiblahUiState.LocationUiState(
                            country = location.country,
                            city = location.state
                        )
                    )
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateDirection(direction: Float) {
        updateState { it.copy(direction = direction) }
    }
}
