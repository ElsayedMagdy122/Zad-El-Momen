package dev.sayed.mehrabalmomen.presentation.screen.maps

interface MapsInteractionListener {
    fun onDetectLocationClicked()
    fun onMapClicked(lat: Double, lng: Double)
    fun onConfirmLocation()
    fun onDismissBottomSheet()
}