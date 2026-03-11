package dev.sayed.mehrabalmomen.presentation.screen.batteryOptimization.components

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
import androidx.compose.material3.Icon
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
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.padding(end = 8.dp),
                painter = painterResource(R.drawable.ic_dashboard_circle_remove),
                contentDescription = null,
                tint = Theme.color.primary.primary
            )
            Text(
                text = localizedString(R.string.settings_steps),
                color = Theme.color.primary.primary,
                style = Theme.textStyle.title.small,
                textAlign = TextAlign.Center
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 24.dp)
        ) {
            for ((index, step) in instructions.withIndex()) {
                BatteryInstructionsItem(
                    text = step,
                    number = index + 1
                )
            }
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
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(Theme.color.surfaces.surface),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number.toString().toLocalizedDigits(language),
                style = Theme.textStyle.label.medium,
                color = Theme.color.primary.primary
            )
        }
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = text,
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.primary
        )
    }
}