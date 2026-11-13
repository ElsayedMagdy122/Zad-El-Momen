package dev.sayed.mehrabalmomen.presentation.screen.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.components.LoadingContainer
import dev.sayed.mehrabalmomen.presentation.components.NoInternetContainer
import dev.sayed.mehrabalmomen.presentation.screen.maps.components.LocationInfoBox
import dev.sayed.mehrabalmomen.presentation.screen.maps.components.MapsFloatingButton
import dev.sayed.mehrabalmomen.presentation.screen.maps.components.MapsHeaderWithMap
import dev.sayed.mehrabalmomen.presentation.screen.maps.components.MapsToast
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MapsScreen(
    navController: NavController,
    viewModel: MapsViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    var currentLatitude by remember { mutableStateOf(0.0) }
    var currentLongitude by remember { mutableStateOf(0.0) }
    var toastData by remember { mutableStateOf<ToastDetails?>(null) }

    HandleMapsEffects(
        viewModel = viewModel,
        onMoveCamera = { lat, lng ->
            currentLatitude = lat
            currentLongitude = lng
        },
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
    LaunchedEffect(Unit) {
        viewModel.onScreenOpened()
    }
    MapsContent(
        state = state,
        latitude = currentLatitude,
        longitude = currentLongitude,
        toastData = toastData,
        onBack = navController::popBackStack,
        onMapClick = viewModel::onMapClicked,
        onDetectLocation = viewModel::onDetectLocationClicked,
        onConfirmLocation = viewModel::onConfirmLocation,
        onDismissBottomSheet = viewModel::onDismissBottomSheet,
        viewModel = viewModel
    )
}

@Composable
private fun HandleMapsEffects(
    viewModel: MapsViewModel,
    onMoveCamera: (Double, Double) -> Unit,
    onBack: () -> Unit,
    onShowToast: (ToastDetails) -> Unit
) {
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is MapsEffect.NavigateToBack -> onBack()

            is MapsEffect.MoveCamera -> {
                onMoveCamera(effect.lat, effect.lng)
            }

            is MapsEffect.ShowToast -> {
                onShowToast(effect.toast)
            }
        }
    }
}

@Composable
private fun MapsContent(
    state: MapsUiState,
    latitude: Double,
    longitude: Double,
    toastData: ToastDetails?,
    onBack: () -> Unit,
    viewModel: MapsViewModel,
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
        val alpha = if (state.mapLoadState == MapsUiState.MapLoadState.Ready) 1f else 0.0f

        MapsHeaderWithMap(
            modifier = Modifier.alpha(alpha),
            latitude = latitude,
            longitude = longitude,
            onBack = onBack,
            onMapClick = onMapClick,
            onMapStateChanged = viewModel::onMapStateChanged
        )

        when (state.mapLoadState) {
            MapsUiState.MapLoadState.Loading -> {
                LoadingContainer(modifier = Modifier.align(Alignment.Center))
            }

            is MapsUiState.MapLoadState.Error -> {
                NoInternetContainer(onRetryClick = {}, modifier = Modifier.align(Alignment.Center))
            }

            MapsUiState.MapLoadState.Ready -> {
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
    }
}
