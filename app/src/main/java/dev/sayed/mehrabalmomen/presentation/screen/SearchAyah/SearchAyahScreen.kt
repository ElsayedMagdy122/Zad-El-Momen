package dev.sayed.mehrabalmomen.presentation.screen.SearchAyah

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
        }
    }

    SearchAyahContent(
        searchType = searchType,
        surahName = surahName,
        searchQuery = state.searchQuery,
        searchResults = state.results,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onBackClick = viewModel::onBackClick
    )
}

@Composable
private fun SearchAyahContent(
    searchType: SearchType,
    surahName: String? = null,
    searchQuery: String,
    searchResults: List<AyahUi>,
    onSearchQueryChange: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val searchPlaceholder = when (searchType) {
        SearchType.QURAN -> localizedString(R.string.search_in_quran)
        SearchType.SURAH -> localizedString(R.string.search_in_surah, surahName ?: "")
    }
    LazyVerticalGrid(
        modifier = modifier.windowInsetsPadding(WindowInsets.systemBars),
        columns = GridCells.Adaptive(320.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            horizontal = 16.dp
        )
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            QuranAppBar(
                title = "",
                onBackClick = onBackClick,
                isSearchMode = true,
                searchText = searchQuery,
                onSearchTextChange = onSearchQueryChange,
                onSearchClose = { onSearchQueryChange("") },
                placeholder = searchPlaceholder,
                modifier = Modifier.fillMaxWidth()
            )
        }


        items(searchResults.size) { index ->
            SearchResultItem(
                ayahUi = searchResults[index],
                showSurahName = searchType == SearchType.QURAN
            )
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
