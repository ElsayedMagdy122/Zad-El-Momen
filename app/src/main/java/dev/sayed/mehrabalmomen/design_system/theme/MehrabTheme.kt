package dev.sayed.mehrabalmomen.design_system.theme

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.sayed.mehrabalmomen.design_system.color.LocalMehrabColor
import dev.sayed.mehrabalmomen.design_system.color.darkThemeColors
import dev.sayed.mehrabalmomen.design_system.color.lightThemeColors
import dev.sayed.mehrabalmomen.design_system.text_style.LocalMehrabTextStyle
import dev.sayed.mehrabalmomen.design_system.text_style.defaultTextStyle

@Composable
fun MehrabTheme(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val theme = if (isDarkTheme) darkThemeColors else lightThemeColors

    CompositionLocalProvider(
        LocalMehrabColor provides theme,
        LocalMehrabTextStyle provides defaultTextStyle,
        LocalIsDark provides isDarkTheme
    ) {
        UpdateStatusBarIconsForTheme()
        content()
    }
}

internal val LocalIsDark = staticCompositionLocalOf { true }

@Composable
private fun UpdateStatusBarIconsForTheme1() {
    val view = LocalView.current
    val window = (view.context as? ComponentActivity)?.window ?: return

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        window.setNavigationBarContrastEnforced(false)
    }

    WindowInsetsControllerCompat(window, view).apply {
        0
        isAppearanceLightStatusBars = !Theme.isDark
        isAppearanceLightNavigationBars = !Theme.isDark
    }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
         WindowCompat.setDecorFitsSystemWindows(window, false)
        window.navigationBarColor = Theme.color.surfaces.surface.toArgb()
    }
}
@Composable
private fun UpdateStatusBarIconsForTheme() {
    val view = LocalView.current
    val isDark = LocalIsDark.current
    val context = view.context

    LaunchedEffect(isDark) {
        val window = (context as? ComponentActivity)?.window ?: return@LaunchedEffect

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val controller = WindowInsetsControllerCompat(window, view)
        controller.isAppearanceLightStatusBars = !isDark
        controller.isAppearanceLightNavigationBars = !isDark

        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
    }
}