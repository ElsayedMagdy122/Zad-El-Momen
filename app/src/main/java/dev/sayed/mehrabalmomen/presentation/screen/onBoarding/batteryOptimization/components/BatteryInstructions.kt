package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits

@Composable
fun BatteryInstructions(
    instructions: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
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
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Theme.color.surfaces.surfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString().toLocalizedDigits(language),
                style = Theme.textStyle.label.medium,
                color = Theme.color.primary.primary
            )
        }
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = text,
            style = Theme.textStyle.body.small,
            color = Theme.color.primary.shadePrimary,
            textAlign = TextAlign.Start
        )
    }
}