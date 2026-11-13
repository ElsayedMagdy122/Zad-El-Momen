package dev.sayed.mehrabalmomen.presentation.screen.maps.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.ic_location_pin
import dev.sayed.mehrabalmomen.presentation.screen.maps.MapsUiState
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun Map(
    latitude: Double,
    longitude: Double,
    onMapClick: (Double, Double) -> Unit,
    onMapStateChanged: (MapsUiState.MapLoadState) -> Unit,
    modifier: Modifier
) {
    LaunchedEffect(Unit) {
        onMapStateChanged(MapsUiState.MapLoadState.Ready)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFE8ECEF))
            .clickable { onMapClick(latitude, longitude) },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(Res.drawable.ic_location_pin),
            contentDescription = "Selected location marker",
            modifier = Modifier.size(46.dp, 58.dp)
        )
    }
}
