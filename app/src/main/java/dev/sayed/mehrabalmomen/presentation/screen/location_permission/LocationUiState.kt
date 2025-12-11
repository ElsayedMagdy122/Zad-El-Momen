package dev.sayed.mehrabalmomen.presentation.screen.location_permission

data class LocationUiState(
    val isLoading: Boolean = false,
    val isButtonEnabled: Boolean = true,
    val isLocationPermissionGranted: Boolean = false,
    val buttonState: LocationButtonState = LocationButtonState.REQUEST_PERMISSION
){
    enum class LocationButtonState(val text: String) {
        REQUEST_PERMISSION("Allow Location Access"),
        LOADING("Loading..."),
        NEXT("Next")
    }
}
