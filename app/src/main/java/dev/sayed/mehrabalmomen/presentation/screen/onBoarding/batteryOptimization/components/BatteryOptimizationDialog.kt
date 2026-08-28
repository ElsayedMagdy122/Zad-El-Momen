package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.PrimaryDialog
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.BatteryOptimizationInteractionListener

@Composable
fun BatteryOptimizationDialog(
    instructions: List<String>,
    onDismiss: () -> Unit,
    listener: BatteryOptimizationInteractionListener,
    modifier: Modifier = Modifier
) {
    PrimaryDialog(
        onDismiss = onDismiss,
        backgroundColor = Theme.color.surfaces.surface
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Theme.color.surfaces.surface)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = null,
                    tint = Theme.color.primary.primary
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = localizedString(R.string.battery_optimization),
                    textAlign = TextAlign.Center,
                    style = Theme.textStyle.title.medium,
                    color = Theme.color.primary.shadePrimary
                )

                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = localizedString(R.string.battery_optimization_description),
                    textAlign = TextAlign.Center,
                    style = Theme.textStyle.body.small,
                    color = Theme.color.secondary.shadeSecondary
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .heightIn(max = 240.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BatteryInstructionsDialog(
                        instructions = instructions
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    modifier = Modifier
                        .padding(bottom = 8.dp, end = 8.dp)
                        .align(Alignment.End)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            listener.onOpenSettingsClicked()
                        },
                    text = localizedString(R.string.open_settings),
                    textAlign = TextAlign.End,
                    style = Theme.textStyle.label.medium,
                    color = Theme.color.primary.primary
                )
            }
        }
    }
}

