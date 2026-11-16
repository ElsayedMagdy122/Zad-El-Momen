package dev.sayed.mehrabalmomen.presentation.screen.home.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeUiState

@Composable
fun HomeAppBar(
    state: HomeUiState,
    modifier: Modifier = Modifier,
    onNotificationClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LocationCarousel(locationUiState = state.location)
        Spacer(modifier = Modifier.weight(1f))
    }
}
