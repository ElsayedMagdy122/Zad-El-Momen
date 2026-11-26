package dev.sayed.mehrabalmomen.presentation.screen.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import dev.sayed.mehrabalmomen.presentation.components.AlarmPermissionsBottomSheet

@Composable
actual fun AlarmPermissionHandler(
    viewModel: HomeViewModel,
    state: HomeUiState
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onScreenOpened()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    if (state.showAlarmPermissionBottomSheet) {
        AlarmPermissionsBottomSheet(
            isNotificationPermissionMissing = state.isNotificationPermissionMissing,
            isExactAlarmPermissionMissing = state.isExactAlarmPermissionMissing,
            isBatteryOptimizationMissing = state.isBatteryOptimizationMissing,
            onGrantNotification = viewModel::onGrantNotificationPermission,
            onGrantExactAlarm = viewModel::onGrantExactAlarmPermission,
            onGrantBattery = viewModel::onGrantBatteryPermission,
            onGrantAll = viewModel::onGrantAlarmPermissions,
            onIgnore = viewModel::onIgnoreAlarmPermissions
        )
    }
}
