package dev.sayed.mehrabalmomen.presentation.screen.maps

import dev.sayed.mehrabalmomen.design_system.component.ToastDetails

sealed interface MapsEffect {
    object NavigateToBack : MapsEffect
    data class MoveCamera(val lat: Double, val lng: Double) : MapsEffect
    data class ShowToast(
        val toast: ToastDetails
    ) : MapsEffect
}