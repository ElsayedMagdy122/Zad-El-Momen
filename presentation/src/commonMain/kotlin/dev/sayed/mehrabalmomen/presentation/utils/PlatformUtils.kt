package dev.sayed.mehrabalmomen.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.UriHandler

expect fun openUrl(url: String, uriHandler: UriHandler)

@Composable
expect fun rememberOpenAppSettings(): () -> Unit

expect fun getDeviceManufacturer(): String

expect fun getAppVersionName(): String
