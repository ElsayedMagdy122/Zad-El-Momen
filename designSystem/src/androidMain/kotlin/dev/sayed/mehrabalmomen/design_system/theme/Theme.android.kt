package dev.sayed.mehrabalmomen.design_system.theme

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

@Composable
actual fun UpdateStatusBarIconsForTheme(isDark: Boolean) {
    val view = LocalView.current
    val window = (view.context as? ComponentActivity)?.window ?: return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        window.setNavigationBarContrastEnforced(false)
    }

    WindowInsetsControllerCompat(window, view).apply {
        isAppearanceLightStatusBars = !isDark
        isAppearanceLightNavigationBars = !isDark
    }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = Theme.color.surfaces.surface.toArgb()
    }
}
