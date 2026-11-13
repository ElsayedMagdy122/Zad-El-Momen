package dev.sayed.mehrabalmomen.presentation.utils

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.UriHandler

actual fun openUrl(url: String, uriHandler: UriHandler) {
    try {
        uriHandler.openUri(url)
    } catch (_: Exception) {}
}

@Composable
actual fun rememberOpenAppSettings(): () -> Unit {
    val context = LocalContext.current
    return remember(context) {
        {
            try {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    }
}

actual fun getDeviceManufacturer(): String {
    return Build.MANUFACTURER.lowercase()
}

actual fun getAppVersionName(): String {
    return "1.0.0"
}
