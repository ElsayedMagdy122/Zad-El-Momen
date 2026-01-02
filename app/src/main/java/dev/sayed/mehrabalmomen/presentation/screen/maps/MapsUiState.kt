package dev.sayed.mehrabalmomen.presentation.screen.maps

data class MapsUiState(
    val selectedLocation: LocationUi? = null,
    val userCurrentLocation: LocationUi? = null,
    val placeName: String = "",
    val addressLine: String = "",
    val isSuccessToast : Boolean = false,
    val isBottomSheetVisible: Boolean = false
) {
    data class LocationUi(
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    )
}