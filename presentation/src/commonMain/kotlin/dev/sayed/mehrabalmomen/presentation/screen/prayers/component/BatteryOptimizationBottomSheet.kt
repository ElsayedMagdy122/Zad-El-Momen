package dev.sayed.mehrabalmomen.presentation.screen.prayers.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.components.PermissionItem
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.background_permission_desc
import zadelmomen.presentation.generated.resources.battery_optimization
import zadelmomen.presentation.generated.resources.battery_optimization_description
import zadelmomen.presentation.generated.resources.battery_settings_title
import zadelmomen.presentation.generated.resources.ic_protected_privacy
import zadelmomen.presentation.generated.resources.ic_warning
import zadelmomen.presentation.generated.resources.overlay_permission_description
import zadelmomen.presentation.generated.resources.overlay_permission_title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryOptimizationBottomSheet(
    isBatteryOptimizationEnabled: Boolean,
    isOverlayPermissionGranted: Boolean,
    batteryInstructions: List<String>,
    onOpenBatterySettings: () -> Unit,
    onOpenOverlaySettings: () -> Unit,
    onDismiss: () -> Unit
) {
    BottomSheetDs(onDismiss = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = localizedString(Res.string.battery_settings_title),
                    style = Theme.textStyle.label.medium,
                    color = Theme.color.primary.shadePrimary
                )
                Text(
                    text = localizedString(Res.string.battery_optimization_description),
                    style = Theme.textStyle.label.medium,
                    color = Theme.color.semantic.shadeTertiary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Battery Optimization Item
            PermissionItem(
                icon = Res.drawable.ic_warning,
                title = Res.string.battery_optimization,
                description = Res.string.background_permission_desc,
                isGranted = !isBatteryOptimizationEnabled,
                onClick = onOpenBatterySettings
            )

            // Overlay Permission Item
            PermissionItem(
                icon = Res.drawable.ic_protected_privacy,
                title = Res.string.overlay_permission_title,
                description = Res.string.overlay_permission_description,
                isGranted = isOverlayPermissionGranted,
                onClick = onOpenOverlaySettings
            )

            if (batteryInstructions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Theme.color.surfaces.surfaceLow)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    batteryInstructions.forEach { instruction ->
                        Text(
                            text = "• $instruction",
                            style = Theme.textStyle.body.small,
                            color = Theme.color.primary.shadePrimary
                        )
                    }
                }
            }
        }
    }
}
