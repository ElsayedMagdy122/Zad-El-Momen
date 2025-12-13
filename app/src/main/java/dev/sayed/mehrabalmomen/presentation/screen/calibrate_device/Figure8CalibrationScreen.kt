package dev.sayed.mehrabalmomen.presentation.screen.calibrate_device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.calibrate_device.component.CardFigureAnimation
import dev.sayed.mehrabalmomen.presentation.screen.calibrate_device.component.stepsCard

data class Steps(
    val icon: Int,
    val title: String,
    val description: String
)

@Composable
fun Figure8CalibrationScreen(
    navController: NavController
) {
    val list = remember {
        listOf(
            Steps(
                icon = R.drawable.ic_phone,
                title = "Step 1: Hold device flat",
                description = "Hold your phone parallel to the ground to establish a baseline."
            ),
            Steps(
                icon = R.drawable.ic_rotated_phone,
                title = "Step 2: Rotate on axis",
                description = "Rotate your phone in a figure-eight motion to calibrate the magnetometer."
            ),
            Steps(
                icon = R.drawable.ic_rotate,
                title = "Step 3: Tilt & rotate",
                description = "Tilt and rotate the device to cover all axes for complete calibration."
            )
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp)
    ) {
        LazyVerticalGrid(
            modifier = Modifier,
            contentPadding = PaddingValues(
                bottom = 90.dp
            ),
            columns = GridCells.Adaptive(320.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                AppBar(
                    onBackClick = {navController.popBackStack()},
                    title = "Calibrate Device",
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CardFigureAnimation(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)

                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "To improve accuracy, rotate your device slowly in a figure-8 motion three times.",
                    textAlign = TextAlign.Center,
                    color = Theme.color.primary.primary,
                    style = Theme.textStyle.label.medium,
                )
            }
            stepsCard(list)
        }
        PrimaryButton(
            text = "Continue",
            onClick = {navController.navigate(Route.QiblahScreen)},
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, top = 16.dp)
        )
    }
}