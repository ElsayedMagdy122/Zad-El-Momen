package dev.sayed.mehrabalmomen.presentation.screen.home.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeUiState

@Composable
fun HomeAppBar(
    state: HomeUiState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 8.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        LocationCarousel(modifier = Modifier.padding(end = 8.dp), locationUiState = state.location)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            modifier = Modifier,
            text = state.hijriDate,
            color = Theme.color.secondary.shadeSecondary,
            style = Theme.textStyle.label.small,
            maxLines = 1
        )
    }
}
