package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits

@Composable
fun BatteryInstructions(
    instructions: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        for ((index, step) in instructions.withIndex()) {
            BatteryInstructionsItem(
                text = step,
                number = index + 1
            )
        }
    }
}

@Composable
private fun BatteryInstructionsItem(
    modifier: Modifier = Modifier,
    text: String,
    number: Int
) {
    val language = LocalAppLocale.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "${number.toString().toLocalizedDigits(language)}",
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.primary
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = text,
            style = Theme.textStyle.body.small,
            color = Theme.color.primary.shadePrimary,
            textAlign = TextAlign.Start
        )
    }
}

@Composable
fun BatteryInstructionsDialog(
    instructions: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        for (step in instructions) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                text = "• $step",
                style = Theme.textStyle.body.small,
                color = Theme.color.secondary.shadeSecondary,
                textAlign = TextAlign.Start
            )
        }
    }
}