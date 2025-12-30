package dev.sayed.mehrabalmomen.design_system.color

import androidx.compose.ui.graphics.Color

val darkThemeColors = MehrabColors(
    primary = Primary(
        primary = Color(0xFFFFFFFF),
        onPrimary = Color(0xFF12141C),
        shadePrimary = Color(0xFFF8FAFC),
    ),
    surfaces = Surfaces(
        surface = Color(0xFF000000),
        surfaceLow = Color(0xFF0E1017),
        surfaceHigh = Color(0xFF12141C)
    ),
    secondary = Secondary(
        secondary = Color(0xFF66481F),
        shadeSecondary = Color(0xFFBEC0CC),
        secondaryText = Color(0xFF997D56)
    ),
    semantic = Semantic(
        disabled = Color(0xFF3E4252),
        textDisabled = Color(0xFF818599),
        error = Color(0xFFFDA29B),
        success = Color(0xFF71cb87),
        warning = Color(0xFFFEC84B),
        shadeTertiary = Color(0XFF818599)
    )
)