package dev.sayed.mehrabalmomen.design_system.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import dev.sayed.mehrabalmomen.design_system.color.LocalMehrabColor
import dev.sayed.mehrabalmomen.design_system.color.darkThemeColors
import dev.sayed.mehrabalmomen.design_system.color.lightThemeColors
import dev.sayed.mehrabalmomen.design_system.text_style.LocalMehrabTextStyle
import dev.sayed.mehrabalmomen.design_system.text_style.getDefaultTextStyle
import dev.sayed.mehrabalmomen.domain.model.AppSettings

@Composable
fun MehrabTheme(
    language: AppSettings.Language = AppSettings.Language.ARABIC,
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val theme = if (isDarkTheme) darkThemeColors else lightThemeColors
    
    val layoutDirection = when (language) {
        AppSettings.Language.ARABIC -> LayoutDirection.Rtl
        else -> LayoutDirection.Ltr
    }

    CompositionLocalProvider(
        LocalLayoutDirection provides layoutDirection,
        LocalMehrabColor provides theme,
        LocalMehrabTextStyle provides getDefaultTextStyle(),
        LocalIsDark provides isDarkTheme,
    ) {
        content()
    }
}
