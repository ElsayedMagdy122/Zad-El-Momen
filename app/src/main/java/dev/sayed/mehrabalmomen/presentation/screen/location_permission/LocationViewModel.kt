package dev.sayed.mehrabalmomen.presentation.screen.location_permission

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.repository.LocationRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class LocationViewModel(
    private val locationRepository: LocationRepository
) : BaseViewModel<LocationUiState, LocationEffect>(LocationUiState()),
    LocationInteractionListener {


    override fun onClickAllowLocationAccess() {
        val state = screenState.value

        if (state.buttonState == LocationUiState.LocationButtonState.NEXT) {
            sendEffect(LocationEffect.NavigateToHome)
            return
        }

        updateState {
            it.copy(
                isLoading = true,
                isButtonEnabled = false,
                buttonState = LocationUiState.LocationButtonState.LOADING
            )
        }

        sendEffect(LocationEffect.RequestLocationPermission)
    }

    fun onLocationGranted() {
        updateState {
            it.copy(
                isLoading = false,
                isButtonEnabled = true,
                buttonState = LocationUiState.LocationButtonState.NEXT
            )
        }
    }

    fun onLocationPermissionGranted() {
        viewModelScope.launch {
            try {
                val location = locationRepository.getCurrentLocation()
                locationRepository.saveLocation(location)
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
                buttonState = LocationUiState.LocationButtonState.REQUEST_PERMISSION
            )
        }
    }
}