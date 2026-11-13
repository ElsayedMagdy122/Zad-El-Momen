package dev.sayed.mehrabalmomen.presentation.screen.maps.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.sayed.mehrabalmomen.presentation.screen.maps.MapsUiState

@Composable
expect fun Map(
    latitude: Double,
    longitude: Double,
    onMapClick: (Double, Double) -> Unit,
    onMapStateChanged: (MapsUiState.MapLoadState) -> Unit,
    modifier: Modifier = Modifier
)

object MapStyle {
    const val BRIGHT = "https://tiles.openfreemap.org/styles/bright"
}
