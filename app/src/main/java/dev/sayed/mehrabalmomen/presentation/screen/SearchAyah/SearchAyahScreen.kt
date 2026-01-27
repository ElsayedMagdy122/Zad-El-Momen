package dev.sayed.mehrabalmomen.presentation.screen.SearchAyah

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.components.QuranAppBar
import dev.sayed.mehrabalmomen.presentation.components.SearchEmptyContainer
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

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

    CollectEffect(viewModel.effect) {
        when (it) {
            SearchAyahEffect.NavigateBack ->
                navController.popBackStack()

            is SearchAyahEffect.NavigateToSurah -> {
                navController.navigate(
                    Route.SurahAyatScreen(
                        surahId = it.surahId,
                        surahName = it.surahName,
                        targetAyahId = it.ayahId
                    )
                )
            }
        }
    }

    SearchAyahContent(
        searchType = searchType,
        surahName = surahName,
        searchQuery = state.searchQuery,
        searchResults = state.results,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onBackClick = viewModel::onBackClick,
        listener = viewModel,
        state = state
    )
}

@Composable
private fun SearchAyahContent(
    state: SearchAyahUiState,
    searchType: SearchType,
    surahName: String? = null,
    searchQuery: String,
    searchResults: List<AyahUi>,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    listener: SearchAyahInteractionListener,
    modifier: Modifier = Modifier
) {

    val searchPlaceholder = when (searchType) {
        SearchType.QURAN -> localizedString(R.string.search_in_quran)
        SearchType.SURAH -> localizedString(R.string.search_in_surah, surahName ?: "")
    }

    val subTitle = when (searchType) {
        SearchType.QURAN -> R.string.start_searching_subtitle_for_ayah
        SearchType.SURAH -> R.string.start_searching_subtitle_for_reciter
    }

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
                    subTitle,
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
                    subTitle,
                    modifier = Modifier.align(Alignment.Center)
                )}
            } else {
                LazyVerticalStaggeredGrid(
                    modifier = modifier
                        .fillMaxSize(),
                    columns = StaggeredGridCells.Adaptive(320.dp),
                    verticalItemSpacing = 8.dp,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(
                        horizontal = 16.dp
                    )
                ) {
                    items(searchResults.size) { index ->
                        val ayah = searchResults[index]
                        SearchResultItem(
                            ayahUi = searchResults[index],
                            showSurahName = searchType == SearchType.QURAN,
                            modifier = Modifier.clickable { listener.onAyahClick(ayah) }
                        )
                    }
                }
            }
        }

    }
}


@Composable
fun SearchResultItem(
    ayahUi: AyahUi,
    showSurahName: Boolean,
    modifier: Modifier = Modifier
) {
    val language = LocalAppLocale.current
    val ayahWord = localizedString(R.string.ayah)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(12.dp)
    ) {

        val headerText = "$ayahWord ${ayahUi.id.toString().toLocalizedDigits(language)}"
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (showSurahName) {
                Text(
                    text = ayahUi.surahName,
                    textAlign = TextAlign.Start,
                    style = Theme.textStyle.label.medium,
                    color = Theme.color.primary.primary
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(3.dp)
                        .clip(CircleShape)
                        .background(Theme.color.semantic.shadeTertiary)
                )
            }
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = headerText,
                textAlign = TextAlign.Start,
                style = Theme.textStyle.label.medium,
                color = Theme.color.primary.primary
            )
        }
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = ayahUi.text,
            textAlign = TextAlign.Start,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.hafs)),
                lineHeight = 22.sp,
                textAlign = TextAlign.Justify,
                textDirection = TextDirection.Rtl,
            ),
            color = Theme.color.secondary.shadeSecondary
        )
    }
}
