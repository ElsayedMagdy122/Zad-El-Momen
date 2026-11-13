package dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberAudioPermissionRequester(): AudioPermissionRequester {
    return remember {
        object : AudioPermissionRequester {
            override val hasPermission: Boolean = true
            override fun requestPermission(onGranted: () -> Unit) {
                onGranted()
            }
        }
    }
}
