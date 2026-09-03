package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions

import org.jetbrains.compose.resources.StringResource
import zad_el_momen.presentation.generated.resources.Res
import zad_el_momen.presentation.generated.resources.*

data class PermissionsUiState(
    val isLocationPermissionGranted: Boolean = false,
    val isNotificationPermissionGranted: Boolean = false,
    val isAlarmPermissionGranted: Boolean = false,
    val isBackgroundPermissionGranted: Boolean = false,
    val isNotificationPermissionRequired: Boolean = true,
    val isAlarmPermissionRequired: Boolean = true,
    val isLoading: Boolean = false,
    val isButtonEnabled: Boolean = false,
    val buttonState: ButtonState = ButtonState.ALLOW,
    val isSuccessToast: Boolean = false
) {
    enum class ButtonState(val res: StringResource) {
        ALLOW(Res.string.allow),
        NEXT(Res.string.next),
        LOADING(Res.string.loading)
    }
}
