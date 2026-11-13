package dev.sayed.mehrabalmomen.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.UriHandler
import platform.Foundation.NSBundle
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.Foundation.NSURL

actual fun openUrl(url: String, uriHandler: UriHandler) {
    try {
        uriHandler.openUri(url)
    } catch (_: Exception) {}
}

@Composable
actual fun rememberOpenAppSettings(): () -> Unit {
    return remember {
        {
            val url = NSURL.URLWithString(UIApplicationOpenSettingsURLString)
            if (url != null) {
                UIApplication.sharedApplication.openURL(url)
            }
        }
    }
}

actual fun getDeviceManufacturer(): String {
    return "apple"
}

actual fun getAppVersionName(): String {
    return NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String ?: "1.0.0"
}
