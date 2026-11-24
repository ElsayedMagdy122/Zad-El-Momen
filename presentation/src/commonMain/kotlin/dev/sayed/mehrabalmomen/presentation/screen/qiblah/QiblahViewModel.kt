package dev.sayed.mehrabalmomen.presentation.screen.qiblah

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.qiblah.QiblahRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlin.math.abs

class QiblahViewModel(
    private val qiblahRepository: QiblahRepository,
    private val locationRepository: LocationRepository,
    private val analyticsTracker: AnalyticsTracker
) : BaseViewModel<QiblahUiState, QiblahEffect>(QiblahUiState()) {

    private var continuousAngle: Float = 0.0f
    private var isFirstAzimuthUpdate = true
    private var isLocationLoaded = false
    private var cachedLocation: Location? = null
    private var compassJob: Job? = null

    init {
        loadLocationAndQiblah()
        observeCompass(null)
    }

    fun onScreenOpened() {
        analyticsTracker.logScreen("qiblah")
    }

    private fun loadLocationAndQiblah() {
        viewModelScope.launch {
            try {
                val location = locationRepository.getLocation()
                cachedLocation = location
                val bearing = qiblahRepository.getQiblahDirection(location).toFloat()

                isLocationLoaded = true
                isFirstAzimuthUpdate = true

                updateState {
                    it.copy(
                        qiblaBearing = bearing,
                        location = QiblahUiState.LocationUiState(
                            country = location.country,
                            city = location.state
                        )
                    )
                }
                observeCompass(location)
            } catch (_: Exception) {
                isLocationLoaded = true
            }
        }
    }

    private fun observeCompass(location: Location?) {
        compassJob?.cancel()
        compassJob = qiblahRepository.observeCompassAzimuth(location)
            .onEach { compassData ->
                if (!isLocationLoaded) return@onEach

                val trueAzimuth = compassData.azimuth
                val qiblaBearing = screenState.value.qiblaBearing
                val targetRelativeAngle = (qiblaBearing - trueAzimuth + 360f) % 360f

                if (isFirstAzimuthUpdate) {
                    continuousAngle = targetRelativeAngle
                    isFirstAzimuthUpdate = false
                } else {
                    continuousAngle = unwrapAngle(continuousAngle, targetRelativeAngle)
                }

                val diff = (qiblaBearing - trueAzimuth + 360f) % 360f
                val shortestDiff = if (diff > 180f) diff - 360f else diff
                val qiblaOffset = abs(shortestDiff)
                val aligned = qiblaOffset <= 5.0f

                updateState {
                    it.copy(
                        deviceAzimuth = trueAzimuth,
                        relativeAngle = continuousAngle,
                        qiblaOffset = qiblaOffset,
                        isAligned = aligned,
                        isLowAccuracy = compassData.isLowAccuracy
                    )
                }
            }
            .catch {
                updateState { it.copy(isCompassAvailable = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun unwrapAngle(previousAngle: Float, newTargetAngle: Float): Float {
        val currentNormalized = (previousAngle % 360f + 360f) % 360f
        var diff = newTargetAngle - currentNormalized
        if (diff < -180f) diff += 360f
        if (diff > 180f) diff -= 360f
        return previousAngle + diff
    }
}
