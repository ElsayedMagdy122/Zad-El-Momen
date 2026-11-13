package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect

@Composable
actual fun HandlePlatformPermissions(
    viewModel: PermissionsViewModel,
    onShowToast: (ToastDetails) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.updateInitialPermissions()
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            PermissionsEffect.RequestLocationPermission -> {
                viewModel.onLocationGranted()
            }

            PermissionsEffect.RequestNotificationPermission -> {
                viewModel.onNotificationPermissionGranted()
            }

            PermissionsEffect.RequestEnableGps -> {
                viewModel.onGpsEnabled()
            }

            is PermissionsEffect.ShowToast -> {
                onShowToast(effect.toast)
            }

            else -> {}
        }
    }
}
