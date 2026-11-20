package dev.sayed.mehrabalmomen.presentation.screen.maps.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.presentation.screen.maps.MapsUiState
import io.github.dellisd.spatialk.geojson.Position
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.ic_location_pin
import org.jetbrains.compose.resources.painterResource

@Composable
fun Map(
    cameraState: CameraState,
    onMapClick: (Position) -> Unit,
    onMapStateChanged: (MapsUiState.MapLoadState) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
            baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
            onMapClick = { point, _ ->
                onMapClick(point)
                ClickResult.Pass
            },
            onMapLoadFailed = { reason ->
                onMapStateChanged(MapsUiState.MapLoadState.Error(reason))
            },
            onMapLoadFinished = {
                onMapStateChanged(MapsUiState.MapLoadState.Ready)
            },
            options = MapOptions(
                gestureOptions = GestureOptions.Standard,
                ornamentOptions = OrnamentOptions.AllDisabled,
                renderOptions = RenderOptions.Standard
            )
        )

        Image(
            painter = painterResource(Res.drawable.ic_location_pin),
            contentDescription = "Selected location marker",
            modifier = Modifier
                .size(46.dp, 58.dp)
                .align(Alignment.Center)
        )
    }
}

object MapStyle {
    const val BRIGHT = "https://tiles.openfreemap.org/styles/bright"
}
