package dev.sayed.mehrabalmomen.presentation.screen.maps.components

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import org.maplibre.android.geometry.LatLng
import org.maplibre.compose.camera.CameraState
import org.maplibre.compose.map.GestureOptions
import org.maplibre.compose.map.MapOptions
import org.maplibre.compose.map.MaplibreMap
import org.maplibre.compose.map.OrnamentOptions
import org.maplibre.compose.map.RenderOptions
import org.maplibre.compose.style.BaseStyle
import org.maplibre.compose.util.ClickResult

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Map(
    cameraState: CameraState,
    onMapClick: (LatLng) -> Unit,
    modifier: Modifier = Modifier
) {

    BoxWithConstraints(modifier = modifier) {
        MaplibreMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
            baseStyle = BaseStyle.Uri(MapStyle.BRIGHT),
            onMapClick = { point, _ ->
                val latLng = LatLng(point.latitude, point.longitude)
                onMapClick(latLng)
                ClickResult.Pass
            },
            options = MapOptions(
                gestureOptions = GestureOptions.Standard,
                ornamentOptions = OrnamentOptions.AllDisabled,
                renderOptions = RenderOptions.Standard
            )
        )
        Image(
            painter = painterResource(R.drawable.ic_location_pin),
            contentDescription = "Selected location marker",
            modifier = Modifier
                .size(46.dp, 58.dp)
                .align(Alignment.Center)
            //   .offset(y = -29.dp)
        )
    }
}

object MapStyle {
    const val BRIGHT = "https://tiles.openfreemap.org/styles/bright"
}