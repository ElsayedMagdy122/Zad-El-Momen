package dev.sayed.mehrabalmomen.presentation.screen.location_permission

import dev.sayed.mehrabalmomen.design_system.component.ToastDetails

sealed interface LocationEffect {
    object RequestLocationPermission : LocationEffect
    object RequestEnableGps : LocationEffect
    object NavigateToHome : LocationEffect
    data class ShowToast(
        val toast: ToastDetails
    ) : LocationEffect
}