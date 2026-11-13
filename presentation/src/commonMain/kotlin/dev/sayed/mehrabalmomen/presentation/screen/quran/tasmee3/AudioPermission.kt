package dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3

import androidx.compose.runtime.Composable

interface AudioPermissionRequester {
    val hasPermission: Boolean
    fun requestPermission(onGranted: () -> Unit)
}

@Composable
expect fun rememberAudioPermissionRequester(): AudioPermissionRequester
