package dev.sayed.mehrabalmomen.presentation.screen.batteryOptimization.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.batteryOptimization.BatteryOptimizationInteractionListener

@Composable
fun BatteryOptimizationActions(listener: BatteryOptimizationInteractionListener) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OpenSettingsButton(listener)
        SkipForNowText(listener)
    }
}

@Composable
private fun OpenSettingsButton(listener: BatteryOptimizationInteractionListener) {

    PrimaryButton(
        modifier = Modifier.fillMaxWidth(),
        text = localizedString(R.string.open_settings),
        onClick = listener::onOpenSettingsClicked
    )
}

@Composable
private fun SkipForNowText(listener: BatteryOptimizationInteractionListener) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) {
                listener.onSkipForNowClicked()
            },
        text = localizedString(R.string.skip_for_now),
        textAlign = TextAlign.Center,
        style = Theme.textStyle.label.medium,
        color = Theme.color.primary.primary
    )
}