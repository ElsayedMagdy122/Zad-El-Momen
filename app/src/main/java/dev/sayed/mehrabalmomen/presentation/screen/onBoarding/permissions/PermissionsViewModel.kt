package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.base.UiText
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PermissionsViewModel(
    private val locationRepository: LocationRepository,
    private val settingsRepository: SettingsRepository,
    private val networkConnectionRepository: NetworkConnectionRepository,
    private val permissionProvider: PermissionProvider
) : BaseViewModel<PermissionsUiState, PermissionsEffect>(PermissionsUiState()), PermissionsInteractionListener {

    override fun onClickAllowLocationAccess() {
        if (permissionProvider.hasLocationPermission()) {
            sendEffect(PermissionsEffect.RequestEnableGps)
        } else {
            sendEffect(PermissionsEffect.RequestLocationPermission)
        }
    }

    override fun onClickAllowNotificationAccess() {
        sendEffect(PermissionsEffect.RequestNotificationPermission)
    }

    override fun onClickAllowAlarmAccess() {
        sendEffect(PermissionsEffect.RequestAlarmPermission)
    }

    override fun onClickAllowBackgroundAccess() {
        sendEffect(PermissionsEffect.RequestBackgroundPermission)
    }

    override fun onClickNext() {
        viewModelScope.launch {
            settingsRepository.setOnboardingComplete()
            sendEffect(PermissionsEffect.NavigateToBatteryOptimizationScreen)
        }
    }

    fun onNotificationPermissionGranted() {
        updateState { it.copy(isNotificationPermissionGranted = true) }
        checkIfAllPermissionsGranted()
    }

    fun updateInitialPermissions() {
        viewModelScope.launch {
            val savedLocation = settingsRepository.observeLocation().first()
            val hasSavedLocation = savedLocation.latitude != 0.0 && savedLocation.longitude != 0.0
            val isLocationGranted = permissionProvider.hasLocationPermission() && hasSavedLocation

            updateState {
                it.copy(
                    isLocationPermissionGranted = isLocationGranted,
                    isNotificationPermissionGranted = permissionProvider.hasNotificationPermission(),
                    isAlarmPermissionGranted = permissionProvider.canScheduleExactAlarms(),
                    isBackgroundPermissionGranted = permissionProvider.isIgnoringBatteryOptimizations(),
                    isNotificationPermissionRequired = permissionProvider.isNotificationPermissionRequired(),
                    isAlarmPermissionRequired = permissionProvider.isExactAlarmPermissionRequired()
                )
            }
            checkIfAllPermissionsGranted()
        }
    }

    private fun checkIfAllPermissionsGranted() {
        val state = screenState.value
        val allGranted = state.isLocationPermissionGranted && 
                        state.isNotificationPermissionGranted && 
                        state.isAlarmPermissionGranted &&
                        state.isBackgroundPermissionGranted &&
                        !state.isLocationLoading
        
        updateState { it.copy(isButtonEnabled = allGranted) }
    }

    fun onLocationGranted() {
        sendEffect(PermissionsEffect.RequestEnableGps)
    }

    fun onGpsEnabled() {
        viewModelScope.launch {
            handleLocationPermissionGranted()
        }
    }

    fun onGpsDenied() {
        updateState {
            it.copy(
                isLocationPermissionGranted = false,
                isLocationLoading = false
            )
        }
        sendEffect(
            PermissionsEffect.ShowToast(
                ToastDetails(
                    title = UiText.StringResource(R.string.error),
                    message = UiText.StringResource(R.string.location_services_disabled),
                    icon = R.drawable.ic_close_circle
                )
            )
        )
        checkIfAllPermissionsGranted()
    }

    private suspend fun handleLocationPermissionGranted() {
        updateState { it.copy(isLocationLoading = true) }
        checkIfAllPermissionsGranted()
        try {
            val isNetworkAvailable = networkConnectionRepository.isCurrentlyConnected()
            if (!isNetworkAvailable) {
                sendEffect(PermissionsEffect.ShowToast(ToastDetails(
                    title = UiText.StringResource(R.string.error),
                    message = UiText.StringResource(R.string.no_internet_connection),
                    icon = R.drawable.ic_close_circle
                )))
                updateState { it.copy(isLocationLoading = false, isLocationPermissionGranted = false) }
                checkIfAllPermissionsGranted()
                return
            }

            val location = locationRepository.getLocation()
            settingsRepository.saveLocation(location)
            updateState {
                it.copy(
                    isLocationLoading = false,
                    isLocationPermissionGranted = true
                )
            }
            checkIfAllPermissionsGranted()
        } catch (e: Exception) {
            updateState { it.copy(isLocationLoading = false, isLocationPermissionGranted = false) }
            sendEffect(PermissionsEffect.ShowToast(ToastDetails(
                title = UiText.StringResource(R.string.error),
                message = UiText.StringResource(R.string.error),
                icon = R.drawable.ic_close_circle
            )))
            checkIfAllPermissionsGranted()
        }
    }

    fun onLocationDenied() {
        updateState {
            it.copy(
                isLocationPermissionGranted = false,
                isLocationLoading = false
            )
        }
        checkIfAllPermissionsGranted()
    }
}
