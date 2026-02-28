package dev.sayed.mehrabalmomen.presentation.screen.radio

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.component.PrimaryToast
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.LoadingContainer
import dev.sayed.mehrabalmomen.presentation.components.NoInternetContainer
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun RadioScreen(navController: NavController, viewModel: RadioChannelsViewModel = koinViewModel()) {

    val state by viewModel.screenState.collectAsStateWithLifecycle()
    var toast by remember { mutableStateOf<ToastDetails?>(null) }
    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is RadioChannelsEffect.ShowToast -> {
                toast = effect.toast
            }
        }
    }

    LaunchedEffect(toast) {
        toast?.let {
            val current = it
            delay(2000)
            if (toast == current) {
                toast = null
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        LoadingContainer(modifier = Modifier.align(Alignment.Center))
    }

    Crossfade(targetState = state) { state ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Theme.color.surfaces.surface)
                .windowInsetsPadding(WindowInsets.systemBars)
        ) {
            when {
                state.isLoading -> {
                    LoadingContainer(modifier = Modifier.align(Alignment.Center))
                }

                state.isNoInternet -> {
                    NoInternetContainer( onRetryClick = { viewModel.getRadioChannels() }, modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    LazyVerticalStaggeredGrid(
                        modifier = Modifier.fillMaxSize(),
                        columns = StaggeredGridCells.Adaptive(320.dp),
                        verticalItemSpacing = 8.dp,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        item(span = StaggeredGridItemSpan.FullLine) {
                            AppBar(
                                title = localizedString(R.string.quran_radio),
                                isBackEnabled = false,
                                onBackClick = {}
                            )
                        }
                        items(state.channels) {
                            ReciterItem(state = it, listener = viewModel)
                        }
                    }
                }
            }
            toast?.let {
                PrimaryToast(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 24.dp), data = it, isSuccess = true
                )
            }
        }
    }

}

@Composable
private fun ReciterItem(
    state: RadioUiState.RadioChannelUiState,
    listener: RadioChannelsInteractionListener,
    modifier: Modifier = Modifier
) {
    val mediaPlayerIcon = if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val reciterName = if (isRtl) state.nameAr else state.nameEn
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (state.selected) Modifier.border(
                    width = 1.dp,
                    color = Theme.color.primary.primary,
                    shape = RoundedCornerShape(12.dp)
                )
                else Modifier
            )
            .background(Theme.color.surfaces.surfaceLow)
            .padding(8.dp)
            .clickable {
                listener.onChannelClick(state.id)
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Theme.color.surfaces.surfaceHigh)
                .clickable {
                    listener.onPlayPauseClick(state.id)
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(mediaPlayerIcon),
                tint = Theme.color.primary.primary,
                contentDescription = ""
            )
        }
        Text(
            text = reciterName,
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}