package dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberAudioPermissionHandler(
    onPermissionGranted: () -> Unit
): AudioPermissionState {
    return remember {
        object : AudioPermissionState {
            override val hasPermission: Boolean get() = true
            override fun requestPermission() {
                onPermissionGranted()
            }
        }
    }
}
