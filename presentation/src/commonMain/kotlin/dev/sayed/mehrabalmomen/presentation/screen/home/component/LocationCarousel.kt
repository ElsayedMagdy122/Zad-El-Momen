package dev.sayed.mehrabalmomen.presentation.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeUiState

@Composable
fun LocationCarousel(
    locationUiState: HomeUiState.LocationUiState,
    modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.wrapContentWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(id = R.drawable.ic_location),
            tint = Theme.color.primary.primary,
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(start = 4.dp),
            text = "${locationUiState.city}, ${locationUiState.country}",
            color = Theme.color.primary.shadePrimary,
            style = Theme.textStyle.label.small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
