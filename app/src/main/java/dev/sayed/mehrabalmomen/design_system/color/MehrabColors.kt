package dev.sayed.mehrabalmomen.design_system.color

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class MehrabColors(
    val primary: Primary,
    val surfaces: Surfaces,
    val secondary: Secondary,
)

data class Primary(
    val primary: Color,
    val shadePrimary: Color,
)

data class Surfaces(
    val surface: Color,
    val surfaceLow: Color,
    val surfaceHigh: Color,
)

data class Secondary(
    val secondary: Color,
    val shadeSecondary: Color,
    val secondaryText: Color
)

internal val LocalMehrabColor = staticCompositionLocalOf { lightThemeColors }