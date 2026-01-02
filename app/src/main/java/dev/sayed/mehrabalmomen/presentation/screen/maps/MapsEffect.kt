package dev.sayed.mehrabalmomen.presentation.screen.maps

import dev.sayed.mehrabalmomen.presentation.screen.location_permission.LocationEffect

sealed interface MapsEffect {
    object NavigateToBack : MapsEffect
    data class MoveCamera(val lat: Double, val lng: Double) : MapsEffect
    data class ShowToast(
        val title: String,
        val message: String,
        val icon: Int
    ) : MapsEffect
}