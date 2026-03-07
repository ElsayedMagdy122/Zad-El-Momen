package dev.sayed.mehrabalmomen.presentation.screen.radio.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.component.PrimaryToast
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.components.LoadingContainer
import dev.sayed.mehrabalmomen.presentation.components.NoInternetContainer
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioChannelsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioUiState

@Composable
fun RadioContent(
    state: RadioUiState,
    viewModel: RadioChannelsViewModel,
    toast: ToastDetails?
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {

        when {

            state.isLoading -> {
                LoadingContainer(Modifier.align(Alignment.Center))
            }

            state.isNoInternet -> {
                NoInternetContainer(
                    onRetryClick = { viewModel.getRadioChannels() },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> {
                RadioChannelsGrid(state, viewModel)
            }
        }

        toast?.let {
            ToastContainer(it)
        }
    }
}

@Composable
private fun BoxScope.ToastContainer(
    toast: ToastDetails
) {

    PrimaryToast(
        modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 24.dp),
        data = toast,
        isSuccess = false
    )
}