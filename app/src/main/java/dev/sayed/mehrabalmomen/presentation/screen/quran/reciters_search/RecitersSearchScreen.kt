package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters_search

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.QuranAppBar
import dev.sayed.mehrabalmomen.presentation.components.SearchEmptyContainer
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.KEY_SELECTED_READER_ID
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.KEY_SELECTED_READER_NAME_AR
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.KEY_SELECTED_READER_NAME_EN
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.components.ReciterItem
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun RecitersSearchScreen(
    navController: NavController,
    viewModel: RecitersSearchViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val appLocale = LocalAppLocale.current
    val isArabic = appLocale == AppSettings.Language.ARABIC

    LaunchedEffect(isArabic) {
        viewModel.setLocale(isArabic)
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            RecitersSearchEffect.NavigateBack -> navController.popBackStack()
            is RecitersSearchEffect.ReciterSelected -> {
                navController.previousBackStackEntry?.savedStateHandle?.apply {
                    set(KEY_SELECTED_READER_ID, effect.readerId)
                    set(KEY_SELECTED_READER_NAME_AR, effect.nameAr)
                    set(KEY_SELECTED_READER_NAME_EN, effect.nameEn)
                }
                navController.popBackStack()
            }
        }
    }

    RecitersSearchContent(
        state = state,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onBackClick = viewModel::onBackClick,
        onReciterClick = { reciter ->
            viewModel.onReciterClick(
                readerId = reciter.id,
                nameAr = reciter.nameAr,
                nameEn = reciter.nameEn
            )
        }
    )
}

@Composable
private fun RecitersSearchContent(
    state: RecitersSearchUiState,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onReciterClick: (dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.ReciterUiState) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        QuranAppBar(
            title = "",
            onBackClick = onBackClick,
            isSearchMode = true,
            searchText = state.searchQuery,
            onSearchTextChange = onSearchQueryChange,
            onSearchClose = { onSearchQueryChange("") },
            placeholder = localizedString(R.string.search_reciter),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )

        if (state.searchQuery.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                SearchEmptyContainer(
                    isStartState = true,
                    isResultsState = false,
                    subtitle = R.string.start_searching_subtitle_for_reciter,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else if (state.results.isEmpty() && !state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                SearchEmptyContainer(
                    isStartState = false,
                    isResultsState = true,
                    subtitle = R.string.start_searching_subtitle_for_reciter,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            LazyVerticalStaggeredGrid(
                modifier = Modifier.fillMaxSize(),
                columns = StaggeredGridCells.Adaptive(320.dp),
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
            ) {
                items(
                    items = state.results,
                    key = { it.id }
                ) { reciter ->
                    ReciterItem(
                        state = reciter,
                        isArabic = state.isArabic,
                        onPlayClick = {  },
                        onDownloadClick = {  },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onReciterClick(reciter) }
                    )
                }
            }
        }
    }
}
