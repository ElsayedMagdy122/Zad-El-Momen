package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import dev.sayed.mehrabalmomen.R
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
import org.koin.androidx.compose.koinViewModel
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings

@Composable
fun PermissionsScreen(
    navController: NavController,
    viewModel: PermissionsViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val locationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineGranted || coarseGranted) {
            viewModel.onLocationGranted()
        } else {
            viewModel.onLocationDenied()
        }
    }

    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.onNotificationPermissionGranted()
        }
    }

    val enableGpsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onLocationGranted()
        } else {
            viewModel.onLocationDenied()
        }
    }

    var toastData by remember { mutableStateOf<ToastDetails?>(null) }

    LaunchedEffect(Unit) {
        viewModel.updateInitialPermissions()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.updateInitialPermissions()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            PermissionsEffect.RequestLocationPermission -> {
                locationLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }

            PermissionsEffect.RequestNotificationPermission -> {
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

            PermissionsEffect.RequestAlarmPermission -> {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }

            PermissionsEffect.RequestBackgroundPermission -> {
                val intent = Intent().apply {
                    action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            }

            PermissionsEffect.NavigateToBatteryOptimizationScreen -> {
                navController.navigate(Route.BatteryOptimizationScreen)
            }

            PermissionsEffect.RequestEnableGps -> {
                val locationRequest = LocationRequest
                    .Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                    .build()

                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                    .setAlwaysShow(true)

                val client = LocationServices.getSettingsClient(context)

                client.checkLocationSettings(builder.build())
                    .addOnFailureListener { exception ->
                        if (exception is ResolvableApiException) {
                            enableGpsLauncher.launch(
                                IntentSenderRequest.Builder(exception.resolution).build()
                            )
                        }
                    }
            }

            is PermissionsEffect.ShowToast -> {
                toastData = effect.toast
            }
        }
    }

    PermissionsContent(
        state = state,
        toastData = toastData,
        listener = viewModel
    )
}

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
                    icon = R.drawable.ic_location,
                    title = R.string.location_permission,
                    description = R.string.location_permission_desc,
                    isGranted = state.isLocationPermissionGranted,
                    onClick = { listener.onClickAllowLocationAccess() }
                )
            }

            if (state.isNotificationPermissionRequired) {
                item {
                    PermissionItem(
                        icon = R.drawable.ic_notifications,
                        title = R.string.notification_permission,
                        description = R.string.notification_permission_desc,
                        isGranted = state.isNotificationPermissionGranted,
                        onClick = { listener.onClickAllowNotificationAccess() }
                    )
                }
            }

            if (state.isAlarmPermissionRequired) {
                item {
                    PermissionItem(
                        icon = R.drawable.ic_reminder,
                        title = R.string.alarm_permission,
                        description = R.string.alarm_permission_desc,
                        isGranted = state.isAlarmPermissionGranted,
                        onClick = { listener.onClickAllowAlarmAccess() }
                    )
                }
            }

            item {
                PermissionItem(
                    icon = R.drawable.ic_protected_privacy,
                    title = R.string.background_permission,
                    description = R.string.background_permission_desc,
                    isGranted = state.isBackgroundPermissionGranted,
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
                    .padding(top = 24.dp),
                durationMillis = 3000L
            )
        }
    }
}
