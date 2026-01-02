package dev.sayed.mehrabalmomen.presentation.screen.maps.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import org.maplibre.compose.camera.CameraState

@Composable fun MapsHeaderWithMap(
    cameraState: CameraState,
    onBack: () -> Unit,
    onMapClick: (Double, Double) -> Unit
) {
    Column {
        AppBar(
            modifier = Modifier.padding(horizontal = 16.dp),
            title = localizedString(R.string.change_location),
            onBackClick = onBack
        )

        Map(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .fillMaxSize(),
            cameraState = cameraState,
            onMapClick = { onMapClick(it.latitude, it.longitude) }
        )
    }
}