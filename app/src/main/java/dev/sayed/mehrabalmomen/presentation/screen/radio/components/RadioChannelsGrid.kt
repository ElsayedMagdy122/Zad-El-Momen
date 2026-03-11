package dev.sayed.mehrabalmomen.presentation.screen.radio.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioChannelsInteractionListener
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioUiState

@Composable
fun RadioChannelsGrid(
    state: RadioUiState,
    listener: RadioChannelsInteractionListener
) {
    val scrollState: LazyGridState = rememberLazyGridState()
    LazyVerticalGrid(
        state = scrollState,
        modifier = Modifier.fillMaxSize(),
        columns = GridCells.Adaptive(320.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    ) {

        item(span = { GridItemSpan(maxLineSpan) }, key = "app_bar") {
            AppBar(
                title = localizedString(R.string.quran_radio),
                isBackEnabled = false,
                onBackClick = {}
            )
        }

        items(state.channels, key = { it.id }) {

            RadioReciterItem(
                state = it,
                listener = listener
            )
        }
    }
}