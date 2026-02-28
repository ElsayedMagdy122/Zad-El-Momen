package dev.sayed.mehrabalmomen.presentation.screen.location_permission

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
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
                        ToastDetails(
                        title =R.string.no_internet_connection,
                        message =R.string.please_connect_to_the_internet_to_continue,
                        icon = R.drawable.ic_close_circle
                    ))
                )
                updateState {
                    it.copy(
                      isSuccessToast = false
                    )
                }
                onLocationDenied()
                return@launch
            }

            tryToCall(
                block = {
                    val location = locationRepository.getLocation()
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