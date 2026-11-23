package dev.sayed.mehrabalmomen.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.components.PermissionItem
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.alarm_permission
import zadelmomen.presentation.generated.resources.alarm_permission_desc
import zadelmomen.presentation.generated.resources.alarm_permissions_desc
import zadelmomen.presentation.generated.resources.alarm_permissions_title
import zadelmomen.presentation.generated.resources.background_permission
import zadelmomen.presentation.generated.resources.background_permission_desc
import zadelmomen.presentation.generated.resources.grant_permissions
import zadelmomen.presentation.generated.resources.ic_alarm_clock
import zadelmomen.presentation.generated.resources.ic_protected_privacy
import zadelmomen.presentation.generated.resources.ic_reminder
import zadelmomen.presentation.generated.resources.skip_for_now
import zadelmomen.presentation.generated.resources.notification_permission
import zadelmomen.presentation.generated.resources.notification_permission_desc

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmPermissionsBottomSheet(
    isNotificationPermissionMissing: Boolean,
    isExactAlarmPermissionMissing: Boolean,
    isBatteryOptimizationMissing: Boolean,
    onGrantNotification: () -> Unit,
    onGrantExactAlarm: () -> Unit,
    onGrantBattery: () -> Unit,
    onGrantAll: () -> Unit,
    onIgnore: () -> Unit,
    onDismiss: () -> Unit = onIgnore
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
                    text = localizedString(Res.string.alarm_permissions_title),
                    style = Theme.textStyle.label.medium,
                    color = Theme.color.primary.shadePrimary
                )
                Text(
                    text = localizedString(Res.string.alarm_permissions_desc),
                    style = Theme.textStyle.label.medium,
                    color = Theme.color.semantic.shadeTertiary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Notification Permission Item
            PermissionItem(
                icon = Res.drawable.ic_alarm_clock,
                title = Res.string.notification_permission,
                description = Res.string.notification_permission_desc,
                isGranted = !isNotificationPermissionMissing,
                showActionButton = true,
                onClick = onGrantNotification
            )

            // Exact Alarm Permission Item
            PermissionItem(
                icon = Res.drawable.ic_reminder,
                title = Res.string.alarm_permission,
                description = Res.string.alarm_permission_desc,
                isGranted = !isExactAlarmPermissionMissing,
                showActionButton = true,
                onClick = onGrantExactAlarm
            )

            // Battery Optimization / Background Permission Item
            PermissionItem(
                icon = Res.drawable.ic_protected_privacy,
                title = Res.string.background_permission,
                description = Res.string.background_permission_desc,
                isGranted = !isBatteryOptimizationMissing,
                showActionButton = true,
                onClick = onGrantBattery
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PrimaryButton(
                    text = localizedString(Res.string.grant_permissions),
                    onClick = onGrantAll,
                    modifier = Modifier.fillMaxWidth()
                )

                PrimaryButton(
                    text = localizedString(Res.string.skip_for_now),
                    onClick = onIgnore,
                    enabledContainerColor = Theme.color.surfaces.surfaceLow,
                    enabledTextColor = Theme.color.primary.shadePrimary,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
