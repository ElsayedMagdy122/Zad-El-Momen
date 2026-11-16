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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.data.location.IosLocationService
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.component.PrimaryToast
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.components.PermissionCard
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.components.PermissionHeader
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.components.PermissionItem
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.coroutines.launch
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.ic_alarm_clock
import zadelmomen.presentation.generated.resources.ic_location
import zadelmomen.presentation.generated.resources.location_permission
import zadelmomen.presentation.generated.resources.location_permission_desc
import zadelmomen.presentation.generated.resources.notification_permission
import zadelmomen.presentation.generated.resources.notification_permission_desc

@Composable
actual fun PermissionsScreen(
    navController: NavController,
    viewModel: PermissionsViewModel
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    var toastData by remember { mutableStateOf<ToastDetails?>(null) }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.updateInitialPermissions()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.updateInitialPermissions()
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            PermissionsEffect.RequestLocationPermission -> scope.launch {
                if (IosLocationService.requestPermission()) viewModel.onLocationGranted()
                else viewModel.onLocationDenied()
            }

            PermissionsEffect.RequestEnableGps -> {
                if (IosLocationService.isLocationServiceEnabled()) viewModel.onGpsEnabled()
                else viewModel.onGpsDenied()
            }

            PermissionsEffect.RequestNotificationPermission -> scope.launch {
                if (IosLocationService.requestNotificationPermission()) {
                    viewModel.onNotificationPermissionGranted()
                }
            }

            PermissionsEffect.NavigateToHome -> {
                navController.navigate(Route.AppRoute) {
                    popUpTo(Route.CalculationMethodScreen) { inclusive = true }
                }
            }

            is PermissionsEffect.ShowToast -> toastData = effect.toast
            else -> {}
        }
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
                    isLoading = state.isLocationLoading,
                    onClick = { viewModel.onClickAllowLocationAccess() }
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
                        onClick = { viewModel.onClickAllowNotificationAccess() }
                    )
                }
            }
        }

        PrimaryButton(
            isLoading = state.isLoading,
            isEnabled = state.isButtonEnabled,
            text = localizedString(state.buttonState.value),
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
