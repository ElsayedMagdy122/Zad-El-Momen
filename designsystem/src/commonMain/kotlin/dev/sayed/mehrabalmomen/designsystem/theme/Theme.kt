package dev.sayed.mehrabalmomen.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import dev.sayed.mehrabalmomen.designsystem.color.LocalMehrabColor
import dev.sayed.mehrabalmomen.designsystem.color.MehrabColors
import dev.sayed.mehrabalmomen.designsystem.text_style.LocalMehrabTextStyle
import dev.sayed.mehrabalmomen.designsystem.text_style.MehrabTextStyle

object Theme {
    val color: MehrabColors
        @Composable @ReadOnlyComposable get() = LocalMehrabColor.current

    val textStyle: MehrabTextStyle
        @Composable @ReadOnlyComposable get() = LocalMehrabTextStyle.current

    val isDark: Boolean
        @Composable @ReadOnlyComposable get() = LocalIsDark.current
}
