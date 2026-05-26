package dev.sayed.mehrabalmomen.design_system.theme

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.result.ActivityResultRegistryOwner
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import dev.sayed.mehrabalmomen.design_system.color.LocalMehrabColor
import dev.sayed.mehrabalmomen.design_system.color.darkThemeColors
import dev.sayed.mehrabalmomen.design_system.color.lightThemeColors
import dev.sayed.mehrabalmomen.design_system.text_style.LocalMehrabTextStyle
import dev.sayed.mehrabalmomen.design_system.text_style.defaultTextStyle
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import java.util.Locale

@Composable
fun MehrabTheme(
    language: AppSettings.Language = AppSettings.Language.ARABIC,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val theme = if (isDarkTheme) darkThemeColors else lightThemeColors
    val context = LocalContext.current
    val localizedContext = remember(language) {
        val locale = Locale(language.code)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.createConfigurationContext(config)
    }
    val layoutDirection = when (language) {
        AppSettings.Language.ARABIC -> LayoutDirection.Rtl
        else -> LayoutDirection.Ltr
    }
    val registryOwner = remember(context) {
        var currentContext = context
        while (currentContext is android.content.ContextWrapper) {
            if (currentContext is ActivityResultRegistryOwner) break
            currentContext = currentContext.baseContext
        }
        currentContext as? ActivityResultRegistryOwner
    }
    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalLayoutDirection provides layoutDirection,
        LocalMehrabColor provides theme,
        LocalMehrabTextStyle provides defaultTextStyle,
        LocalIsDark provides isDarkTheme,
        LocalActivityResultRegistryOwner provides registryOwner!!,
    ) {
        UpdateStatusBarIconsForTheme()
        content()
    }
}

internal val LocalIsDark = staticCompositionLocalOf { true }

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