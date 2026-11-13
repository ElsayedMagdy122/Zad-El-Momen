package dev.sayed.mehrabalmomen.presentation.screen.maps

data class MapsUiState(
    val selectedLocation: LocationUi? = null,
    val userCurrentLocation: LocationUi? = null,
    val placeName: String = "",
    val addressLine: String = "",
    val isSuccessToast: Boolean = false,
    val isBottomSheetVisible: Boolean = false,
    val mapLoadState: MapLoadState = MapLoadState.Loading,
) {
    data class LocationUi(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    )

    sealed interface MapLoadState {
        object Loading : MapLoadState
        object Ready : MapLoadState
        data class Error(val reason: String?) : MapLoadState
    }
}