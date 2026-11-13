package dev.sayed.mehrabalmomen.presentation.screen.maps.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.change_location
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.maps.MapsUiState

@Composable
fun MapsHeaderWithMap(
    latitude: Double,
    longitude: Double,
    onBack: () -> Unit,
    onMapClick: (Double, Double) -> Unit,
    onMapStateChanged: (MapsUiState.MapLoadState) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        AppBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = localizedString(Res.string.change_location),
            onBackClick = onBack
        )

        Map(
            latitude = latitude,
            longitude = longitude,
            modifier = Modifier.fillMaxSize(),
            onMapClick = onMapClick,
            onMapStateChanged = onMapStateChanged
        )
    }
}
