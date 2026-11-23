package dev.sayed.mehrabalmomen.presentation.screen.home

import androidx.compose.runtime.Composable

@Composable
actual fun AlarmPermissionHandler(
    effect: HomeEffect?,
    onPermissionsUpdated: () -> Unit
) {
    // No-op for iOS platform
}
