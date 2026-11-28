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
actual fun rememberAudioPermissionHandler(
    onPermissionGranted: () -> Unit
): AudioPermissionState {
    val context = LocalContext.current
    var hasPermissionState by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermissionState = isGranted
        if (isGranted) {
            onPermissionGranted()
        }
    }

    return remember(hasPermissionState, launcher) {
        object : AudioPermissionState {
            override val hasPermission: Boolean
                get() = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED

            override fun requestPermission() {
                if (hasPermission) {
                    onPermissionGranted()
                } else {
                    launcher.launch(Manifest.permission.RECORD_AUDIO)
                }
            }
        }
    }
}
