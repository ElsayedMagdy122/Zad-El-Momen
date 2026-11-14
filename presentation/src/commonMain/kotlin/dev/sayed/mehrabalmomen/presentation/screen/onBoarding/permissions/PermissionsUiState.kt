package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions

data class PermissionsUiState(
    val isLoading: Boolean = false,
    val isLocationLoading: Boolean = false,
    val isButtonEnabled: Boolean = false,
    val isLocationPermissionGranted: Boolean = false,
    val isNotificationPermissionGranted: Boolean = false,
    val isAlarmPermissionGranted: Boolean = false,
    val isBackgroundPermissionGranted: Boolean = false,
    val isNotificationPermissionRequired: Boolean = false,
    val isAlarmPermissionRequired: Boolean = false,
    val isSuccessToast: Boolean = false,
    val buttonState: PermissionButtonState = PermissionButtonState.NEXT
) {
    enum class PermissionButtonState {
        LOADING,
        NEXT
    }
}
