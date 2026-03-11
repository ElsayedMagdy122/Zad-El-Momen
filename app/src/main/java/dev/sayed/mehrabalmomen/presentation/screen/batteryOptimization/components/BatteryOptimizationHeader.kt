package dev.sayed.mehrabalmomen.presentation.screen.batteryOptimization.components

import androidx.compose.runtime.Composable
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.batteryOptimization.BatteryOptimizationInteractionListener

@Composable
fun BatteryOptimizationHeader(listener: BatteryOptimizationInteractionListener) {
    AppBar(
        title = localizedString(R.string.battery_optimization),
        onBackClick = listener::onBackClicked
    )
}