package dev.sayed.mehrabalmomen.presentation.screen.SearchAyah

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.text_style.Hafs
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.QuranAppBar
import dev.sayed.mehrabalmomen.presentation.components.SearchEmptyContainer
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import dev.sayed.mehrabalmomen.presentation.utils.toLocalizedDigits
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
enum class SearchType {
    QURAN, SURAH
}

@Composable
fun SearchAyahScreen(
    navController: NavController,
    searchType: SearchType,
    surahId: Int?,
    surahName: String?,
    viewModel: SearchAyahViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            SearchAyahEffect.NavigateBack -> {
                navController.popBackStack()
            }

            is SearchAyahEffect.NavigateToSurah -> {
                navController.navigate(
                    Route.SurahAyatScreen(
                        surahId = effect.surahId,
                        arabicName = effect.surahName,
                        englishName = effect.surahName,
                        targetAyahId = effect.ayahId
                    )
                )
            }
        }
    }

    SearchAyahContent(
        state = state,
        searchType = searchType,
        surahName = surahName,
        searchQuery = state.searchQuery,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onAyahClick = viewModel::onAyahClick,
        onBackClick = viewModel::onBackClick
    )
}

@Composable
fun SearchAyahContent(
    state: SearchAyahUiState,
    searchType: SearchType,
    surahName: String?,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAyahClick: (AyahUi) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val searchPlaceholder = when (searchType) {
        SearchType.QURAN -> localizedString(Res.string.search_in_quran)
        SearchType.SURAH -> localizedString(Res.string.search_in_surah, surahName ?: "")
    }

    val subTitle = Res.string.start_searching_subtitle_for_ayah

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars),
    ) {
        QuranAppBar(
            title = "",
            onBackClick = onBackClick,
            isSearchMode = true,
            searchText = searchQuery,
            onSearchTextChange = onSearchQueryChange,
            onSearchClose = { onSearchQueryChange("") },
            placeholder = searchPlaceholder,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
        )
        if (state.searchQuery.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                SearchEmptyContainer(
                    isStartState = true,
                    isResultsState = false,
                    subtitle = subTitle,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        } else {
            if (state.results.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    SearchEmptyContainer(
                        isStartState = false,
                        isResultsState = true,
                        subtitle = subTitle,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Adaptive(320.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalItemSpacing = 8.dp,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 16.dp
                    ),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.results) { ayah ->
                        AyahSearchItem(
                            ayah = ayah,
                            onClick = { onAyahClick(ayah) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AyahSearchItem(
    ayah: AyahUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appLanguage = LocalAppLocale.current
    val ayahWord = localizedString(Res.string.ayah)
    val surahAndAyah = "${ayah.surahName} | $ayahWord ${ayah.id.toLocalizedDigits(appLanguage)}"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = surahAndAyah,
                color = Theme.color.primary.primary,
                style = Theme.textStyle.label.medium,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = ayah.text,
                color = Theme.color.secondary.secondary,
                style = TextStyle(
                    fontFamily = Hafs,
                    fontSize = 20.sp,
                    lineHeight = 36.sp,
                    textAlign = TextAlign.Start,
                    textDirection = TextDirection.Content
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
