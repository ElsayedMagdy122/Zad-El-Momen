package dev.sayed.mehrabalmomen.presentation.screen.quran.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahUiState

@Composable
fun SurahGrid(
    sur: List<SurahUiState>,
    onSurahClick: (Int, String,String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(320.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            bottom = 8.dp
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                modifier = Modifier.padding(bottom = 4.dp),
                text = localizedString(R.string.sur),
                style = Theme.textStyle.title.small,
                color = Theme.color.primary.shadePrimary
            )
        }
        items(sur) { surah ->
            SurahItem(
                surahUiState = surah,
                onClick = onSurahClick
            )
        }
    }
}