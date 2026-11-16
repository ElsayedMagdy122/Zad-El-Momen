package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.PrimaryToast
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.AppBarAction
import dev.sayed.mehrabalmomen.presentation.components.LoadingContainer
import dev.sayed.mehrabalmomen.presentation.components.NoDataContainer
import dev.sayed.mehrabalmomen.presentation.components.NoInternetContainer
import dev.sayed.mehrabalmomen.presentation.components.QuranAppBar
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.components.ReciterItem
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecitersScreen(
    navController: NavController,
    viewModel: RecitersViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    var toast by remember { mutableStateOf<ToastDetails?>(null) }
    val appLocale = LocalAppLocale.current
    val isArabic = appLocale == AppSettings.Language.ARABIC

    LaunchedEffect(Unit) {
        viewModel.loadReciters(isArabic = isArabic)
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            is RecitersEffect.ShowToast -> toast = effect.toast

            RecitersEffect.NavigateBack -> {
                navController.popBackStack()
            }

            is RecitersEffect.NavigateToRecitersSearch -> {
                navController.navigate(
                    Route.RecitersSearchScreen(
                        surahId = effect.surahId,
                        currentReaderId = effect.currentReaderId
                    )
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            QuranAppBar(
                onBackClick = {
                    navController.popBackStack()
                },
                titleColor = Theme.color.primary.shadePrimary,
                title = localizedString(R.string.reciters),
                actions = listOf(
                    AppBarAction(
                        icon = painterResource(R.drawable.ic_search),
                        onClick = {
                            viewModel.onClickSearch()
                        },
                    )
                )
            )

            val screenDisplayState = when {
                state.isLoading -> ReciterScreenDisplayState.LOADING
                state.isNoInternet && state.reciters.isEmpty() -> ReciterScreenDisplayState.NO_INTERNET
                state.reciters.isEmpty() -> ReciterScreenDisplayState.NO_DATA
                else -> ReciterScreenDisplayState.CONTENT
            }

            AnimatedContent(
                targetState = screenDisplayState,
                transitionSpec = {
                    (fadeIn(animationSpec = tween(400)) + scaleIn(initialScale = 0.92f))
                        .togetherWith(fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.95f))
                },
                label = "ScreenStateTransition",
                modifier = Modifier.weight(1f)
            ) { displayState ->
                when (displayState) {
                    ReciterScreenDisplayState.LOADING -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            LoadingContainer()
                        }
                    }

                    ReciterScreenDisplayState.NO_INTERNET -> {
                        NoInternetContainer(
                            onRetryClick = { viewModel.loadReciters(isArabic, isManualRetry = true) }
                        )
                    }

                    ReciterScreenDisplayState.NO_DATA -> {
                        NoDataContainer(
                            onDownloadNow = { viewModel.loadReciters(isArabic, isManualRetry = true) }
                        )
                    }

                    ReciterScreenDisplayState.CONTENT -> {
                        RecitersContent(
                            modifier = Modifier.padding(top = 12.dp),
                            state = state,
                            isArabic = isArabic,
                            onPlayClick = { reciterId -> viewModel.onPlayClick(reciterId) },
                            onDownloadClick = { reciterId -> viewModel.onDownloadClick(reciterId) },
                            onRowSelected = { reciter ->
                                viewModel.onReciterSelected(readerId = reciter.id)
                            }
                        )
                    }
                }
            }
        }

        toast?.let {
            PrimaryToast(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp),
                data = it,
                isSuccess = false
            )
        }
    }
}

@Composable
fun RecitersContent(
    state: RecitersUiState,
    onPlayClick: (Int) -> Unit,
    onDownloadClick: (Int) -> Unit,
    onRowSelected: (ReciterUiState) -> Unit,
    modifier: Modifier = Modifier,
    isArabic: Boolean = true
) {
    LazyVerticalStaggeredGrid(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues( vertical = 12.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        columns = StaggeredGridCells.Adaptive(320.dp)
    ) {
        items(
            items = state.reciters,
            key = { reciter -> reciter.id }
        ) { reciter ->
            ReciterItem(
                state = reciter,
                isArabic = isArabic,
                onPlayClick = { onPlayClick(reciter.id) },
                onDownloadClick = { onDownloadClick(reciter.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRowSelected(reciter) }
            )
        }
    }
}

private enum class ReciterScreenDisplayState {
    LOADING, NO_INTERNET, NO_DATA, CONTENT
}
