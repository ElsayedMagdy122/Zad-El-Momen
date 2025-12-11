package dev.sayed.mehrabalmomen.presentation.screen.location_permission

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.repository.LocationRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class LocationViewModel(
    private val locationRepository: LocationRepository
) : BaseViewModel<LocationUiState, LocationEffect>(LocationUiState()),
    LocationInteractionListener {

    init {
        getSavedLocation()
    }

    fun getSavedLocation() {
        viewModelScope.launch {
            delay(8.seconds)
            val location = locationRepository.getSavedLocation()
            println("SAYED Location : ${location}")
        }
    }

    override fun onClickAllowLocationAccess() {
        val state = screenState.value

        if (state.buttonText == "Next") {
            sendEffect(LocationEffect.NavigateToHome)
            return
        }

        updateState {
            it.copy(
                isLoading = true,
                isButtonEnabled = false
            )
        }

        sendEffect(LocationEffect.RequestLocationPermission)
    }

    fun onLocationGranted() {
        updateState {
            it.copy(
                isLoading = false,
                isButtonEnabled = true,
                buttonText = "Next"
            )
        }
    }

    fun onLocationPermissionGranted() {
        // عند الموافقة على البيرمشن، جلب الموقع
        viewModelScope.launch {
            try {
                val location = locationRepository.getCurrentLocation()
                locationRepository.saveLocation(location) // حفظ الموقع
                onLocationGranted()
            } catch (e: Exception) {
                onLocationDenied()
            }
        }
    }

    fun onLocationDenied() {
        updateState {
            it.copy(
                isLoading = false,
                isButtonEnabled = true,
                buttonText = "Allow Location Access"
            )
        }
    }
}