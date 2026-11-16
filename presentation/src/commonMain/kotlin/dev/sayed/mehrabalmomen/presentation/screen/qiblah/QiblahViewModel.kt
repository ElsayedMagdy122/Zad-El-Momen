package dev.sayed.mehrabalmomen.presentation.screen.qiblah

import android.hardware.GeomagneticField
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.qiblah.QiblahRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
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

    private var magneticDeclination: Float = 0.0f
    private var continuousAngle: Float = 0.0f
    private var isFirstAzimuthUpdate = true
    private var isLocationLoaded = false

    init {
        loadLocationAndQiblah()
        observeCompass()
    }

    fun onScreenOpened() {
        analyticsTracker.logScreen("qiblah")
    }

    private fun loadLocationAndQiblah() {
        viewModelScope.launch {
            try {
                val location = locationRepository.getLocation()
                val bearing = qiblahRepository.getQiblahDirection(location).toFloat()

                magneticDeclination = calculateMagneticDeclination(location)
                
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
            } catch (e: Exception) {
                isLocationLoaded = true
            }
        }
    }

    private fun observeCompass() {
        qiblahRepository.observeCompassAzimuth()
            .onEach { magneticAzimuth ->
                if (!isLocationLoaded) return@onEach

                val trueAzimuth = (magneticAzimuth + magneticDeclination + 360f) % 360f
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
                        isAligned = aligned
                    )
                }
            }
            .catch {
                updateState { it.copy(isCompassAvailable = false) }
            }
            .launchIn(viewModelScope)
    }

    private fun calculateMagneticDeclination(location: Location): Float {
        if (location.latitude == 0.0 && location.longitude == 0.0) return 0f
        return try {
            val field = GeomagneticField(
                location.latitude.toFloat(),
                location.longitude.toFloat(),
                0f,
                System.currentTimeMillis()
            )
            field.declination
        } catch (e: Exception) {
            0f
        }
    }

    private fun unwrapAngle(previousAngle: Float, newTargetAngle: Float): Float {
        val currentNormalized = (previousAngle % 360f + 360f) % 360f
        var diff = newTargetAngle - currentNormalized
        if (diff < -180f) diff += 360f
        if (diff > 180f) diff -= 360f
        return previousAngle + diff
    }
}
