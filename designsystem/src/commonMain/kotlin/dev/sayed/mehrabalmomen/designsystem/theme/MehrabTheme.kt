package dev.sayed.mehrabalmomen.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import dev.sayed.mehrabalmomen.designsystem.color.LocalMehrabColor
import dev.sayed.mehrabalmomen.designsystem.color.darkThemeColors
import dev.sayed.mehrabalmomen.designsystem.color.lightThemeColors
import dev.sayed.mehrabalmomen.designsystem.text_style.LocalMehrabTextStyle
import dev.sayed.mehrabalmomen.designsystem.text_style.defaultTextStyle
import dev.sayed.mehrabalmomen.domain.model.AppSettings

internal val LocalIsDark = staticCompositionLocalOf { true }

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
        LocalMehrabTextStyle provides defaultTextStyle,
        LocalIsDark provides isDarkTheme,
    ) {
        content()
    }
}
