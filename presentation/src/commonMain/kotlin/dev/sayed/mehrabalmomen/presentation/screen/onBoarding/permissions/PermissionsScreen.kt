package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.component.PrimaryToast
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.components.PermissionCard
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.components.PermissionHeader
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.components.PermissionItem
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PermissionsScreen(
    navController: NavController,
    viewModel: PermissionsViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    var toastData by remember { mutableStateOf<ToastDetails?>(null) }

    HandlePlatformPermissions(
        viewModel = viewModel,
        onShowToast = { toastData = it }
    )

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            PermissionsEffect.NavigateToBatteryOptimizationScreen -> {
                navController.navigate(Route.BatteryOptimizationScreen)
            }

            is PermissionsEffect.ShowToast -> {
                toastData = effect.toast
            }

            else -> {}
        }
    }

    PermissionsContent(
        state = state,
        toastData = toastData,
        listener = viewModel
    )
}

@Composable
expect fun HandlePlatformPermissions(
    viewModel: PermissionsViewModel,
    onShowToast: (ToastDetails) -> Unit
)

@Composable
private fun PermissionsContent(
    state: PermissionsUiState,
    toastData: ToastDetails?,
    listener: PermissionsInteractionListener
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp, top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                PermissionCard()
                Spacer(modifier = Modifier.height(16.dp))
                PermissionHeader()
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                PermissionItem(
                    icon = Res.drawable.ic_location,
                    title = Res.string.location_permission,
                    description = Res.string.location_permission_desc,
                    isGranted = state.isLocationPermissionGranted,
                    isLoading = state.isLocationLoading,
                    onClick = { listener.onClickAllowLocationAccess() }
                )
            }

            if (state.isNotificationPermissionRequired) {
                item {
                    PermissionItem(
                        icon = Res.drawable.ic_alarm_clock,
                        title = Res.string.notification_permission,
                        description = Res.string.notification_permission_desc,
                        isGranted = state.isNotificationPermissionGranted,
                        isOptional = true,
                        onClick = { listener.onClickAllowNotificationAccess() }
                    )
                }
            }

            if (state.isAlarmPermissionRequired) {
                item {
                    PermissionItem(
                        icon = Res.drawable.ic_reminder,
                        title = Res.string.alarm_permission,
                        description = Res.string.alarm_permission_desc,
                        isGranted = state.isAlarmPermissionGranted,
                        onClick = { listener.onClickAllowAlarmAccess() }
                    )
                }
            }

            item {
                PermissionItem(
                    icon = Res.drawable.ic_protected_privacy,
                    title = Res.string.background_permission,
                    description = Res.string.background_permission_desc,
                    isGranted = state.isBackgroundPermissionGranted,
                    isOptional = true,
                    onClick = { listener.onClickAllowBackgroundAccess() }
                )
            }
        }

        PrimaryButton(
            isLoading = state.isLoading,
            isEnabled = state.isButtonEnabled,
            text = localizedString(state.buttonState.value),
            onClick = { listener.onClickNext() },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )

        toastData?.let {
            PrimaryToast(
                data = it,
                isSuccess = state.isSuccessToast,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
            )
        }
    }
}
