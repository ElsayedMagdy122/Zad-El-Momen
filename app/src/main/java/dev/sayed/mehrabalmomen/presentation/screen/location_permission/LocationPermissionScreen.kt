package dev.sayed.mehrabalmomen.presentation.screen.location_permission

import android.Manifest
import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.calibrate_device.Steps
import dev.sayed.mehrabalmomen.presentation.screen.calibrate_device.component.stepsCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun LocationPermissionScreen(
    navController: NavController,
    viewModel: LocationViewModel = koinViewModel()
) {
    val list = remember {
        listOf(
            Steps(
                icon = R.drawable.ic_accurate_prayer,
                title = (R.string.accurate_prayer_times),
                description = (R.string.precise_calculation_based_on_your_location)
            ),
            Steps(
                icon = R.drawable.ic_qiblah,
                title = (R.string.qibla_direction_step),
                description = (R.string.guidance_to_the_kaaba_for_your_prayers)
            ),
            Steps(
                icon = R.drawable.ic_protected_privacy,
                title = (R.string.protected_privacy),
                description = (R.string.your_data_is_safe_and_secured)
            )
        )
    }
    val state by viewModel.screenState.collectAsState()
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        if (fineGranted || coarseGranted) {
            viewModel.onLocationPermissionGranted()
        } else {
            viewModel.onLocationDenied()
        }
    }
    val context = LocalContext.current
    val enableGpsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onLocationPermissionGranted()
        } else {
            viewModel.onLocationDenied()
        }
    }
    var toastData by remember { mutableStateOf<ToastDetails?>(null) }
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LocationEffect.RequestLocationPermission -> {
                    launcher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }

                LocationEffect.NavigateToHome -> {
                    // navController.navigate(Route.HomeScreen)
                    navController.navigate(Route.HomeScreen) {
                        popUpTo(Route.MadhabScreen) { inclusive = true }
                    }
                }

                LocationEffect.RequestEnableGps -> {
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

                is LocationEffect.ShowToast -> {
                    toastData = ToastDetails(
                        title = effect.title,
                        message = effect.message,
                        icon = effect.icon
                    )
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            Modifier
                .align(Alignment.TopCenter)
        ) {
            LazyVerticalGrid(
                modifier = Modifier,
                contentPadding = PaddingValues(
                    bottom = 90.dp,
                    top = 64.dp
                ),
                columns = GridCells.Adaptive(320.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        LocationCard()
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LocationHeaders()
                }
                stepsCard(list)
            }
        }
        PrimaryButton(
            isLoading = state.isLoading,
            isEnabled = state.isButtonEnabled,
            text = stringResource(state.buttonState.value),
            onClick = { viewModel.onClickAllowLocationAccess() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )

        toastData?.let {
            PrimaryToast(
                data = it,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp),
                durationMillis = 3000L
            )
        }
    }
}

@Composable
fun LocationCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            painter = painterResource(id = R.drawable.ic_location),
            contentDescription = null,
            tint = Theme.color.primary.primary
        )
    }
}

@Composable
fun LocationHeaders(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.location_permission),
            color = Theme.color.primary.primary,
            style = Theme.textStyle.title.medium
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(R.string.we_need_your_location_to_calculate_accurate_prayer_times_and_determine_the_qibla_direction),
            color = Theme.color.primary.primary,
            style = Theme.textStyle.label.medium,
            textAlign = TextAlign.Center
        )
    }
}