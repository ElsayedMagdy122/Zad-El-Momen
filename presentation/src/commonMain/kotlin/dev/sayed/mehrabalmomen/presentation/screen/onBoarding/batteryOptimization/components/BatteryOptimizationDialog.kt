package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.components.PrimaryDialog
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.BatteryOptimizationInteractionListener
import org.jetbrains.compose.resources.painterResource

@Composable
fun BatteryOptimizationDialog(
    instructions: List<String>,
    onDismiss: () -> Unit,
    listener: BatteryOptimizationInteractionListener,
    modifier: Modifier = Modifier,
    isOverlayPermissionGranted: Boolean = true
) {
    PrimaryDialog(
        onDismiss = onDismiss,
        backgroundColor = Theme.color.surfaces.surface
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Theme.color.surfaces.surfaceLow)
                    .clickable { onDismiss() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_close),
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
                    text = localizedString(Res.string.battery_settings_title),
                    textAlign = TextAlign.Center,
                    style = Theme.textStyle.title.medium,
                    color = Theme.color.primary.shadePrimary,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    modifier = Modifier.padding(top = 4.dp),
                    text = localizedString(Res.string.battery_optimization_description),
                    textAlign = TextAlign.Center,
                    style = Theme.textStyle.body.small,
                    color = Theme.color.secondary.shadeSecondary
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .heightIn(max = 340.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (!isOverlayPermissionGranted) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Theme.color.surfaces.surfaceHigh)
                                .border(
                                    1.dp,
                                    Theme.color.semantic.warning.copy(alpha = 0.6f),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.ic_warning),
                                        contentDescription = null,
                                        tint = Theme.color.semantic.warning,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = localizedString(Res.string.overlay_permission_title),
                                        style = Theme.textStyle.title.small,
                                        color = Theme.color.semantic.warning,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = localizedString(Res.string.overlay_permission_description),
                                    style = Theme.textStyle.body.small,
                                    color = Theme.color.secondary.shadeSecondary,
                                    textAlign = TextAlign.Start
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.End)
                                        .padding(top = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Theme.color.primary.primary)
                                        .clickable { listener.onOpenOverlaySettingsClicked() }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = localizedString(Res.string.enable_overlay_permission),
                                        style = Theme.textStyle.label.medium,
                                        color = Theme.color.primary.onPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    BatteryInstructionsDialog(
                        instructions = instructions
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Theme.color.primary.primary)
                            .clickable {
                                listener.onOpenSettingsClicked()
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = localizedString(Res.string.open_settings),
                            textAlign = TextAlign.Center,
                            style = Theme.textStyle.label.medium,
                            color = Theme.color.primary.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
