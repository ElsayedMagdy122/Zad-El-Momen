package dev.sayed.mehrabalmomen.presentation.screen.maps


import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import java.util.Locale

@SuppressLint("MissingPermission")
@Composable
fun MapsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }
    var placeName by remember { mutableStateOf("Selected Location") }
    var addressLine by remember { mutableStateOf("") }

    fun reverseGeocode(latLng: LatLng) {
        scope.launch {
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                val addresses: List<Address>? =
                    geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (!addresses.isNullOrEmpty()) {
                    val addr = addresses[0]
                    placeName = addr.locality ?: addr.subAdminArea ?: addr.featureName ?: "Makkah"
                    addressLine = addr.getAddressLine(0)
                        ?: "${addr.thoroughfare ?: ""}, ${addr.subLocality ?: ""}".trim(',', ' ')
                    if (addressLine.isBlank()) addressLine = "King Abdulaziz Rd, Al Haram, 24231"
                } else {
                    placeName = "Makkah, Saudi Arabia"
                    addressLine = "King Abdulaziz Rd, Al Haram, 24231"
                }
            } catch (e: Exception) {
                placeName = "Makkah, Saudi Arabia"
                addressLine = "King Abdulaziz Rd, Al Haram, 24231"
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                MapView(context).apply {
                    getMapAsync { map ->
                        mapLibreMap = map
                        map.setStyle(
                            Style.Builder().fromUri("https://tiles.openfreemap.org/styles/liberty")
                        )
                        map.addOnMapClickListener { point ->
                            val latLng = LatLng(point.latitude, point.longitude)
                            selectedLatLng = latLng

                            map.clear()
                            map.addMarker(
                                MarkerOptions().position(latLng).title("Selected Location")
                            )

                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0))

                            reverseGeocode(latLng)
                            true
                        }
                        map.uiSettings.apply {
                            isZoomGesturesEnabled = true
                            isScrollGesturesEnabled = true
                            isRotateGesturesEnabled = true
                            isTiltGesturesEnabled = true
                            isAttributionEnabled = false

                        }

                    }
                }
            }
        )

        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            onClick = {
                detectUserLocation(
                    context = context,
                    map = mapLibreMap
                ) { latLng ->
                    selectedLatLng = latLng
                    reverseGeocode(latLng)
                }
            }
        ) {
            Icon(Icons.Default.LocationOn, contentDescription = "My Location")
        }
        selectedLatLng?.let {
            LocationInfoBox(
                placeName = placeName,
                addressLine = addressLine,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                onConfirm = {
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LocationInfoBox(
    placeName: String,
    addressLine: String,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = placeName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = addressLine,
                fontSize = 16.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "Confirm Location →",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun detectUserLocation(
    context: Context,
    map: MapLibreMap?,
    onDetected: (LatLng) -> Unit
) {
    if (map == null) return

    val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    fusedClient.lastLocation.addOnSuccessListener { location ->
        location ?: return@addOnSuccessListener

        val latLng = LatLng(location.latitude, location.longitude)

        map.clear()
        map.addMarker(
            MarkerOptions().position(latLng)
                .title("My Location")
                .snippet("This is my location")
        )

        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15.0))

        onDetected(latLng)
    }
}
