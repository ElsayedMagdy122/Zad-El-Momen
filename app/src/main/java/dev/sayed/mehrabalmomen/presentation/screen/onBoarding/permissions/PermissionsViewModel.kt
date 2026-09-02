package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class PermissionsViewModel(
    private val locationRepository: LocationRepository,
    private val settingsRepository: SettingsRepository,
    private val networkConnectionRepository: NetworkConnectionRepository,
    private val permissionProvider: PermissionProvider
) : BaseViewModel<PermissionsUiState, PermissionsEffect>(PermissionsUiState()), PermissionsInteractionListener {

    override fun onClickAllowLocationAccess() {
        sendEffect(PermissionsEffect.RequestLocationPermission)
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
        updateState {
            it.copy(
                isLocationPermissionGranted = permissionProvider.hasLocationPermission(),
                isNotificationPermissionGranted = permissionProvider.hasNotificationPermission(),
                isAlarmPermissionGranted = permissionProvider.canScheduleExactAlarms(),
                isBackgroundPermissionGranted = permissionProvider.isIgnoringBatteryOptimizations(),
                isNotificationPermissionRequired = permissionProvider.isNotificationPermissionRequired(),
                isAlarmPermissionRequired = permissionProvider.isExactAlarmPermissionRequired()
            )
        }
        checkIfAllPermissionsGranted()
    }

    private fun checkIfAllPermissionsGranted() {
        val state = screenState.value
        val allGranted = state.isLocationPermissionGranted && 
                        state.isNotificationPermissionGranted && 
                        state.isAlarmPermissionGranted &&
                        state.isBackgroundPermissionGranted
        
        updateState { it.copy(isButtonEnabled = allGranted) }
    }

    fun onLocationGranted() {
        viewModelScope.launch {
            handleLocationPermissionGranted()
        }
    }

    private suspend fun handleLocationPermissionGranted() {
        updateState { it.copy(isLoading = true) }
        try {
            val isNetworkAvailable = networkConnectionRepository.isCurrentlyConnected()
            if (!isNetworkAvailable) {
                sendEffect(PermissionsEffect.ShowToast(ToastDetails(
                    title = R.string.error,
                    message = R.string.no_internet_connection,
                    icon = R.drawable.ic_close_circle
                )))
                updateState { it.copy(isLoading = false, isLocationPermissionGranted = false) }
                return
            }

            val location = locationRepository.getLocation()
            settingsRepository.saveLocation(location)
            updateState {
                it.copy(
                    isLoading = false,
                    isLocationPermissionGranted = true
                )
            }
            checkIfAllPermissionsGranted()
        } catch (e: Exception) {
            updateState { it.copy(isLoading = false, isLocationPermissionGranted = false) }
            sendEffect(PermissionsEffect.ShowToast(ToastDetails(
                title = R.string.error,
                message = R.string.error,
                icon = R.drawable.ic_close_circle
            )))
        }
    }

    fun onLocationDenied() {
        updateState {
            it.copy(
                isLocationPermissionGranted = false,
                isLoading = false
            )
        }
        checkIfAllPermissionsGranted()
    }
}
