package dev.sayed.mehrabalmomen.presentation.screen.home.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeUiState

@Composable
fun HomeAppBar(
    locationUiState: HomeUiState.LocationUiState,
    onClickSettings: () -> Unit,
    modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.prayer_times),
            color = Theme.color.primary.primary,
            style = Theme.textStyle.title.medium,
            modifier = modifier.weight(1f)
        )
        LocationCarousel(locationUiState = locationUiState)
        Icon(
            modifier = Modifier.padding(start = 8.dp).clickable(onClick = {onClickSettings()},indication = null, interactionSource = null),
            painter = painterResource(id = R.drawable.settings_ic),
            tint = Theme.color.primary.primary,
            contentDescription = null
        )
    }
}
