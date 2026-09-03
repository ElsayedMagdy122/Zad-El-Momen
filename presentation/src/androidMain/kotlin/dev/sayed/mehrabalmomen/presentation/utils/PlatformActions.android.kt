package dev.sayed.mehrabalmomen.presentation.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun HandlePlatformEffects(effect: Any?, onEffectHandled: () -> Unit) {
    // This will be specialized for each screen if needed, 
    // or we can have a generic one.
}

actual fun openStoreReview() {
    // Need context, maybe via a static reference or Koin
}

fun Context.findActivity(): Activity? {
    var currentContext = this
    while (currentContext is ContextWrapper) {
        if (currentContext is Activity) {
            return currentContext
        }
        currentContext = currentContext.baseContext
    }
    return currentContext as? Activity
}
