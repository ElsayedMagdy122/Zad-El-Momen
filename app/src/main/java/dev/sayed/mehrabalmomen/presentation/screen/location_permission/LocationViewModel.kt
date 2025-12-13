package dev.sayed.mehrabalmomen.presentation.screen.location_permission

import dev.sayed.mehrabalmomen.domain.repository.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel

class LocationViewModel(
    private val locationRepository: LocationRepository,
    private val settingsRepository: SettingsRepository
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
        tryToCall(
            block = {
                val location = locationRepository.getCurrentLocation()
                settingsRepository.saveLocation(location.latitude,location.longitude)
            },
            onSuccess = {
                onLocationGranted()
                settingsRepository.setOnboardingComplete()
            },
            onError = {
                sendEffect(LocationEffect.RequestEnableGps)
            }
        )
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