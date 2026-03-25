package dev.sayed.mehrabalmomen.presentation.screen.radio.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.components.LoadingContainer
import dev.sayed.mehrabalmomen.presentation.components.NoInternetContainer
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioChannelsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioUiState

@Composable
fun RadioChannelsGrid(
    state: RadioUiState,
    viewModel: RadioChannelsViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState: LazyGridState = rememberLazyGridState()
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
                LazyVerticalGrid(
                    state = scrollState,
                    modifier = modifier.fillMaxSize(),
                    columns = GridCells.Adaptive(320.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
                ) {

                    items(state.channels, key = { it.id }) {

                        RadioReciterItem(
                            state = it,
                            listener = viewModel
                        )
                    }
                }
            }
        }
    }
}