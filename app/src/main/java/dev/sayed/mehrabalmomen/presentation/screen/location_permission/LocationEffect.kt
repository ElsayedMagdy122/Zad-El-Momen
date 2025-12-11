package dev.sayed.mehrabalmomen.presentation.screen.location_permission

sealed interface LocationEffect {
    object RequestLocationPermission : LocationEffect
    object NavigateToHome : LocationEffect
}