package dev.sayed.mehrabalmomen.presentation.screen.radio.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
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

    LazyVerticalStaggeredGrid(
        modifier = Modifier.fillMaxSize(),
        columns = StaggeredGridCells.Adaptive(320.dp),
        verticalItemSpacing = 8.dp,
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        )
    ) {

        item(span = StaggeredGridItemSpan.FullLine) {

            AppBar(
                title = localizedString(R.string.quran_radio),
                isBackEnabled = false,
                onBackClick = {}
            )
        }

        items(state.channels) {

            RadioReciterItem(
                state = it,
                listener = listener
            )
        }
    }
}