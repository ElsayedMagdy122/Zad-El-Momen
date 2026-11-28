package dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3

import androidx.compose.runtime.Composable

interface AudioPermissionState {
    val hasPermission: Boolean
    fun requestPermission()
}

@Composable
expect fun rememberAudioPermissionHandler(
    onPermissionGranted: () -> Unit
): AudioPermissionState
