package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions

import dev.sayed.mehrabalmomen.design_system.component.ToastDetails

sealed interface PermissionsEffect {
    data object RequestLocationPermission : PermissionsEffect
    data object RequestNotificationPermission : PermissionsEffect
    data object RequestAlarmPermission : PermissionsEffect
    data object RequestBackgroundPermission : PermissionsEffect
    data object RequestEnableGps : PermissionsEffect
    data object NavigateToBatteryOptimizationScreen : PermissionsEffect
    data class ShowToast(
        val toast: ToastDetails
    ) : PermissionsEffect
}
