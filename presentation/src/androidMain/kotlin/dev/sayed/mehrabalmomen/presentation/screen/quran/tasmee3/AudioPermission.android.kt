package dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun rememberAudioPermissionRequester(): AudioPermissionRequester {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var pendingGrantedAction by remember { mutableStateOf<(() -> Unit)?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            pendingGrantedAction?.invoke()
            pendingGrantedAction = null
        }
    }

    return remember(hasPermission) {
        object : AudioPermissionRequester {
            override val hasPermission: Boolean
                get() = hasPermission

            override fun requestPermission(onGranted: () -> Unit) {
                if (hasPermission) {
                    onGranted()
                } else {
                    pendingGrantedAction = onGranted
                    launcher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }
    }
}
