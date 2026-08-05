package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.component.PrimaryToast
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.components.LoadingContainer
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.KEY_SELECTED_READER_ID
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.KEY_SELECTED_READER_NAME_AR
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.KEY_SELECTED_READER_NAME_EN
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

            is RecitersEffect.ReciterSelected -> {
                navController.previousBackStackEntry?.savedStateHandle?.apply {
                    set(KEY_SELECTED_READER_ID, effect.readerId)
                    set(KEY_SELECTED_READER_NAME_AR, effect.nameAr)
                    set(KEY_SELECTED_READER_NAME_EN, effect.nameEn)
                }
                navController.popBackStack()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        AnimatedContent(
            targetState = state.isLoading,
            transitionSpec = {
                fadeIn(animationSpec = tween(400)) + scaleIn(initialScale = 0.92f) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            label = "LoadingToContentAnimation"
        ) { isLoading ->
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingContainer()
                }
            } else {
                RecitersContent(
                    state = state,
                    isArabic = isArabic,
                    onPlayClick = { reciterId -> viewModel.onPlayClick(reciterId) },
                    onDownloadClick = { /* reciterId -> ... */ },
                    onRowSelected = { reciter ->
                        viewModel.onReciterSelected(
                            readerId = reciter.id,
                            nameAr = reciter.nameAr,
                            nameEn = reciter.nameEn
                        )
                    }
                )
            }
        }

        toast?.let {
            PrimaryToast(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp), data = it, isSuccess = false
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
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
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