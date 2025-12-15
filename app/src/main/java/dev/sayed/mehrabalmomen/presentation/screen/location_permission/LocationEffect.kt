package dev.sayed.mehrabalmomen.presentation.screen.location_permission

sealed interface LocationEffect {
    object RequestLocationPermission : LocationEffect
    object RequestEnableGps : LocationEffect
    object NavigateToHome : LocationEffect
    data class ShowToast(
        val title: String,
        val message: String,
        val icon: Int
    ) : LocationEffect
}