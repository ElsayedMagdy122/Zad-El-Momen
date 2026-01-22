package dev.sayed.mehrabalmomen.presentation.screen.maps


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.maps.components.LocationInfoBox
import dev.sayed.mehrabalmomen.presentation.screen.maps.components.MapsFloatingButton
import dev.sayed.mehrabalmomen.presentation.screen.maps.components.MapsHeaderWithMap
import dev.sayed.mehrabalmomen.presentation.screen.maps.components.MapsToast
import io.github.dellisd.spatialk.geojson.Position
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.camera.rememberCameraState
import kotlin.time.Duration.Companion.milliseconds


@Composable
fun MapsScreen(
    navController: NavController,
    viewModel: MapsViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsState()
    val cameraState = rememberCameraState()
    var toastData by remember { mutableStateOf<ToastDetails?>(null) }

    HandleMapsEffects(
        viewModel = viewModel,
        cameraState = cameraState,
        onBack = navController::popBackStack,
        onShowToast = { toastData = it }
    )
    LaunchedEffect(toastData) {
        toastData?.let {
            val current = it
            delay(2000)
            if (toastData == current) {
                toastData = null
            }
        }
    }
    MapsContent(
        state = state,
        cameraState = cameraState,
        toastData = toastData,
        onBack = navController::popBackStack,
        onMapClick = viewModel::onMapClicked,
        onDetectLocation = viewModel::onDetectLocationClicked,
        onConfirmLocation = viewModel::onConfirmLocation,
        onDismissBottomSheet = viewModel::onDismissBottomSheet
    )
}

@Composable
private fun HandleMapsEffects(
    viewModel: MapsViewModel,
    cameraState: CameraState,
    onBack: () -> Unit,
    onShowToast: (ToastDetails) -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MapsEffect.NavigateToBack -> onBack()

                is MapsEffect.MoveCamera -> {
                    cameraState.animateTo(
                        CameraPosition(
                            target = Position(effect.lng, effect.lat),
                            zoom = 15.0
                        ),
                        duration = 800.milliseconds
                    )
                }

                is MapsEffect.ShowToast -> {
                    onShowToast(
                       effect.toast
                    )
                }
            }
        }
    }
}

@Composable
private fun MapsContent(
    state: MapsUiState,
    cameraState: CameraState,
    toastData: ToastDetails?,
    onBack: () -> Unit,
    onMapClick: (Double, Double) -> Unit,
    onDetectLocation: () -> Unit,
    onConfirmLocation: () -> Unit,
    onDismissBottomSheet: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {

        MapsHeaderWithMap(
            cameraState = cameraState,
            onBack = onBack,
            onMapClick = onMapClick
        )

        MapsFloatingButton(onDetectLocation)

        toastData?.let {
            MapsToast(it, state.isSuccessToast)
        }

        if (state.isBottomSheetVisible) {
            LocationInfoBox(
                placeName = state.placeName,
                addressLine = state.addressLine,
                onConfirm = onConfirmLocation,
                onDismiss = onDismissBottomSheet
            )
        }
    }
}