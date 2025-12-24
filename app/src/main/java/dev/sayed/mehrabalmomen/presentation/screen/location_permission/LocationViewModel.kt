package dev.sayed.mehrabalmomen.presentation.screen.location_permission

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.repository.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class LocationViewModel(
    private val locationRepository: LocationRepository,
    private val settingsRepository: SettingsRepository,
    private val networkConnectionRepository: NetworkConnectionRepository
) : BaseViewModel<LocationUiState, LocationEffect>(LocationUiState()),
    LocationInteractionListener {

    override fun onClickAllowLocationAccess() {
        val state = screenState.value

        when (state.buttonState) {
            LocationUiState.LocationButtonState.NEXT -> {
                sendEffect(LocationEffect.NavigateToHome)
            }

            LocationUiState.LocationButtonState.REQUEST_PERMISSION -> {
                updateState {
                    it.copy(
                        isLoading = true,
                        isButtonEnabled = false,
                        buttonState = LocationUiState.LocationButtonState.LOADING
                    )
                }
                sendEffect(LocationEffect.RequestLocationPermission)
            }

            else -> {
            }
        }
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

            val isConnected = networkConnectionRepository.isCurrentlyConnected()

            if (!isConnected) {
                sendEffect(
                    LocationEffect.ShowToast(
                        title = "No Internet Connection",
                        message = "Please connect to the internet to continue",
                        icon = R.drawable.check
                    )
                )
                onLocationDenied()
                return@launch
            }

            tryToCall(
                block = {
                    val location = locationRepository.getCurrentLocation()
                    settingsRepository.saveLocation(location)
                },
                onSuccess = {
                    onLocationGranted()
                    settingsRepository.setOnboardingComplete()
                },
                onError = {
                    sendEffect(LocationEffect.RequestEnableGps)
                    onLocationDenied()
                }
            )
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