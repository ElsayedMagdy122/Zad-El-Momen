package dev.sayed.mehrabalmomen.presentation.screen.qiblah

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.components.AppBar
import dev.sayed.mehrabalmomen.presentation.screen.qiblah.components.DirectionCard
import dev.sayed.mehrabalmomen.presentation.screen.qiblah.components.KaabaOnCircle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun QiblahScreen(
    navController: NavController,
    viewModel: QiblahViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsState()
    val animatedDirection by animateFloatAsState(targetValue = state.direction)

    HandleCompassSensor(viewModel = viewModel)

    QiblahScreenContent(
        navController = navController,
        direction = animatedDirection
    )

}

@Composable
private fun HandleCompassSensor(viewModel: QiblahViewModel) {
    val context = LocalContext.current
    val sensorManager =
        remember { context.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

    DisposableEffect(Unit) {

        val listener = object : SensorEventListener {

            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {

                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)

                    val azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
                    val normalized = (azimuth + 360f) % 360f

                    viewModel.updateDirection(normalized)
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        if (rotationSensor != null) {
            sensorManager.registerListener(
                listener,
                rotationSensor,
                SensorManager.SENSOR_DELAY_GAME
            )
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }
}

@Composable
private fun QiblahScreenContent(
    navController: NavController,
    direction: Float
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp)
            .verticalScroll(state = rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        AppBar(
            onBackClick = { navController.popBackStack() },
            title = stringResource(R.string.qiblah),
        )

        KaabaOnCircle(
            directionDegrees = direction,
            modifier = Modifier.padding(vertical = 64.dp)
        )

        DirectionCard(direction = direction)
    }
}