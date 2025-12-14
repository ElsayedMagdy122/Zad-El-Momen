package dev.sayed.mehrabalmomen.presentation.screen.location_permission

sealed interface LocationEffect {
    object RequestLocationPermission : LocationEffect
    object RequestEnableGps : LocationEffect
    object NavigateToHome : LocationEffect
}