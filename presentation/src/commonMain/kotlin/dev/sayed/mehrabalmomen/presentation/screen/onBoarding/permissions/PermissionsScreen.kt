package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.component.PrimaryToast
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.components.PermissionCard
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.components.PermissionHeader
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.components.PermissionItem
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import dev.sayed.mehrabalmomen.presentation.utils.rememberPermissionManager
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import zad_el_momen.presentation.generated.resources.Res
import zad_el_momen.presentation.generated.resources.*

@Composable
fun PermissionsScreen(
    onNavigateToBatteryOptimization: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: PermissionsViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    var toastData by remember { mutableStateOf<ToastDetails?>(null) }
    val permissionManager = rememberPermissionManager()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            PermissionsEffect.RequestLocationPermission -> {
                permissionManager.requestLocationPermission { granted ->
                    if (granted) viewModel.onLocationGranted()
                    else viewModel.onLocationDenied()
                }
            }
            PermissionsEffect.RequestNotificationPermission -> {
                permissionManager.requestNotificationPermission { granted ->
                    if (granted) viewModel.onNotificationPermissionGranted()
                }
            }
            PermissionsEffect.RequestAlarmPermission -> {
                permissionManager.requestExactAlarmPermission()
            }
            PermissionsEffect.RequestBackgroundPermission -> {
                permissionManager.requestIgnoreBatteryOptimization()
            }
            PermissionsEffect.NavigateToBatteryOptimizationScreen -> {
                if (state.isAlarmPermissionRequired) {
                    onNavigateToBatteryOptimization()
                } else {
                    onNavigateToHome()
                }
            }
            is PermissionsEffect.ShowToast -> toastData = effect.toast
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        viewModel.updateInitialPermissions()
    }

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
                    onClick = { viewModel.onClickAllowLocationAccess() }
                )
            }

            if (state.isNotificationPermissionRequired) {
                item {
                    PermissionItem(
                        icon = Res.drawable.ic_notifications,
                        title = Res.string.notification_permission,
                        description = Res.string.notification_permission_desc,
                        isGranted = state.isNotificationPermissionGranted,
                        onClick = { viewModel.onClickAllowNotificationAccess() }
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
                        onClick = { viewModel.onClickAllowAlarmAccess() }
                    )
                }
            }

            if (state.isAlarmPermissionRequired) {
                 item {
                    PermissionItem(
                        icon = Res.drawable.ic_protected_privacy,
                        title = Res.string.background_permission,
                        description = Res.string.background_permission_desc,
                        isGranted = state.isBackgroundPermissionGranted,
                        onClick = { viewModel.onClickAllowBackgroundAccess() }
                    )
                }
            }
        }

        PrimaryButton(
            isLoading = state.isLoading,
            isEnabled = state.isButtonEnabled,
            text = stringResource(state.buttonState.res),
            onClick = { viewModel.onClickNext() },
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
                    .padding(top = 24.dp),
                durationMillis = 3000L
            )
        }
    }
}
