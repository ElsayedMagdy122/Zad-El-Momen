package dev.sayed.mehrabalmomen.design_system.color

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class MehrabColors(
    val primary: Primary,
    val surfaces: Surfaces,
    val secondary: Secondary,
    val semantic : Semantic
)

data class Primary(
    val primary: Color,
    val onPrimary: Color,
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

data class Semantic(
    val disabled : Color,
    val textDisabled : Color,
    val shadeTertiary : Color,
    val error : Color,
    val warning : Color,
    val success : Color
)
internal val LocalMehrabColor = staticCompositionLocalOf { lightThemeColors }