package dev.sayed.mehrabalmomen.presentation.screen.quran.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahUiState

@Composable
fun SurahGrid(
    sur: List<SurahUiState>,
    onSurahClick: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(320.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(sur) { surah ->
            SurahItem(
                surahUiState = surah,
                onClick = onSurahClick
            )
        }
    }
}