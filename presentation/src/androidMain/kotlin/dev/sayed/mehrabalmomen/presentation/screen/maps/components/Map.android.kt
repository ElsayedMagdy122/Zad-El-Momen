package dev.sayed.mehrabalmomen.presentation.screen.maps.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.ic_location_pin
import dev.sayed.mehrabalmomen.presentation.screen.maps.MapsUiState
import org.jetbrains.compose.resources.painterResource
import org.maplibre.compose.camera.CameraPosition
import org.maplibre.compose.camera.rememberCameraState
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult
import io.github.dellisd.spatialk.geojson.Position
import kotlin.time.Duration.Companion.milliseconds

@Composable
actual fun Map(
    latitude: Double,
    longitude: Double,
    onMapClick: (Double, Double) -> Unit,
    onMapStateChanged: (MapsUiState.MapLoadState) -> Unit,
    modifier: Modifier
) {
    val cameraState = rememberCameraState()

    LaunchedEffect(latitude, longitude) {
        if (latitude != 0.0 || longitude != 0.0) {
            cameraState.animateTo(
                CameraPosition(
                    target = Position(longitude, latitude),
                    zoom = 15.0
                ),
                duration = 800.milliseconds
            )
        }
    }

    BoxWithConstraints(modifier = modifier) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
            baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
            onMapClick = { point, _ ->
                onMapClick(point.latitude, point.longitude)
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
