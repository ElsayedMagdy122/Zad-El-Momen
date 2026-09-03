package dev.sayed.mehrabalmomen.presentation.screen.calibrate_device

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.calibrate_device.component.CardFigureAnimation
import dev.sayed.mehrabalmomen.presentation.screen.calibrate_device.component.stepsCard

data class Steps(
    val icon: Int,
    val title: Int,
    val description: Int
)

@Composable
fun Figure8CalibrationScreen(
    navController: NavController
) {
    val list = remember {
        listOf(
            Steps(
                icon = R.drawable.ic_phone,
                title = (R.string.step_1_hold_device_flat),
                description = (R.string.hold_your_phone_parallel_to_the_ground_to_establish_a_baseline)
            ),
            Steps(
                icon = R.drawable.ic_rotated_phone,
                title = (R.string.step_2_rotate_on_axis),
                description = (R.string.rotate_your_phone_in_a_figure_eight_motion_to_calibrate_the_magnetometer)
            ),
            Steps(
                icon = R.drawable.ic_rotate,
                title = (R.string.step_3_tilt_rotate),
                description = (R.string.tilt_and_rotate_the_device_to_cover_all_axes_for_complete_calibration)
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
                    onBackClick = { navController.popBackStack() },
                    title = localizedString(R.string.calibrate_device),
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
                    text = localizedString(R.string.to_improve_accuracy_rotate_your_device_slowly_in_a_figure_8_motion_three_times),
                    textAlign = TextAlign.Center,
                    color = Theme.color.primary.primary,
                    style = Theme.textStyle.label.medium,
                )
            }
            stepsCard(list)
        }
        PrimaryButton(
            text = localizedString(R.string.btn_continue),
            onClick = {
                navController.navigate(Route.QiblahScreen) {
                    launchSingleTop = true
                    popUpTo(Route.CalibrateDevice) {
                        inclusive = true
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, top = 16.dp)
        )
    }
}