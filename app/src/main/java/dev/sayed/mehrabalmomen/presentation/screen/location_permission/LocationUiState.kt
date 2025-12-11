package dev.sayed.mehrabalmomen.presentation.screen.location_permission

data class LocationUiState(
    val isLoading: Boolean = false,
    val isButtonEnabled: Boolean = true,
    val isLocationPermissionGranted: Boolean = false,
    val buttonText: String = "Allow Location Access"
)
