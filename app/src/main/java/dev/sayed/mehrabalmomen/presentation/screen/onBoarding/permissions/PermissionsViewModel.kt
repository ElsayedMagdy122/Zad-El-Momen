package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions

import android.os.Build
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.repository.location.LocationRepository
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class PermissionsViewModel(
    private val locationRepository: LocationRepository,
    private val settingsRepository: SettingsRepository,
    private val networkConnectionRepository: NetworkConnectionRepository
) : BaseViewModel<PermissionsUiState, PermissionsEffect>(PermissionsUiState()),
    PermissionsInteractionListener {

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
            sendEffect(PermissionsEffect.NavigateToBatteryOptimizationScreen)
        }
    }

    fun onNotificationPermissionGranted() {
        updateState { it.copy(isNotificationPermissionGranted = true) }
        checkIfAllPermissionsGranted()
    }

    fun onAlarmPermissionGranted() {
        updateState { it.copy(isAlarmPermissionGranted = true) }
        checkIfAllPermissionsGranted()
    }

    fun onBackgroundPermissionGranted() {
        updateState { it.copy(isBackgroundPermissionGranted = true) }
        checkIfAllPermissionsGranted()
    }

    fun updateInitialPermissions(
        isLocationGranted: Boolean,
        isNotificationGranted: Boolean,
        isAlarmGranted: Boolean,
        isBackgroundGranted: Boolean
    ) {
        updateState {
            it.copy(
                isLocationPermissionGranted = isLocationGranted,
                isNotificationPermissionGranted = isNotificationGranted,
                isAlarmPermissionGranted = isAlarmGranted,
                isBackgroundPermissionGranted = isBackgroundGranted
            )
        }
        checkIfAllPermissionsGranted()
    }

    private fun checkIfAllPermissionsGranted() {
        val state = screenState.value
        val isNotificationRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
        val isAlarmRequired = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

        val notificationStatus = if (isNotificationRequired) state.isNotificationPermissionGranted else true
        val alarmStatus = if (isAlarmRequired) state.isAlarmPermissionGranted else true

        val allGranted = state.isLocationPermissionGranted &&
                notificationStatus &&
                alarmStatus &&
                state.isBackgroundPermissionGranted

        updateState { it.copy(isButtonEnabled = allGranted) }
    }

    fun onLocationGranted() {
        updateState {
            it.copy(
                isLoading = false,
                isLocationPermissionGranted = true
            )
        }
        checkIfAllPermissionsGranted()
    }

    fun onLocationPermissionGranted() {
        viewModelScope.launch {
            val isConnected = networkConnectionRepository.isCurrentlyConnected()
            if (!isConnected) {
                sendEffect(
                    PermissionsEffect.ShowToast(
                        ToastDetails(
                            title = R.string.no_internet_connection,
                            message = R.string.please_connect_to_the_internet_to_continue,
                            icon = R.drawable.ic_close_circle
                        )
                    )
                )
                updateState { it.copy(isSuccessToast = false) }
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
                },
                onError = {
                    sendEffect(PermissionsEffect.RequestEnableGps)
                    onLocationDenied()
                }
            )
        }
    }

    fun onLocationDenied() {
        updateState {
            it.copy(
                isLoading = false,
                isLocationPermissionGranted = false
            )
        }
        checkIfAllPermissionsGranted()
    }
}
