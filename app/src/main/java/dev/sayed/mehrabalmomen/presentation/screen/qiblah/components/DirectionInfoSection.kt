package dev.sayed.mehrabalmomen.presentation.screen.qiblah.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.qiblah.QiblahUiState
import kotlin.math.roundToInt

@Composable
fun DirectionInfoSection(
    locationUiState: QiblahUiState.LocationUiState,
    direction: Float
) {
    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.primary.primary)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = localizedString(R.string.qibla_direction),
            color = Theme.color.secondary.secondaryText,
            style = Theme.textStyle.label.medium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${direction.roundToInt()}°",
            color = Theme.color.surfaces.surfaceLow,
            style = Theme.textStyle.title.large,
            textAlign = TextAlign.Center
        )

        Text(
            text = "${locationUiState.city}, ${locationUiState.country}",
            color = Theme.color.surfaces.surfaceHigh,
            style = Theme.textStyle.label.small,
            textAlign = TextAlign.Center
        )
    }
}