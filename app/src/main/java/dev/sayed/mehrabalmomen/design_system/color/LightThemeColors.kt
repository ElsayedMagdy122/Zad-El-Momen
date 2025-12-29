package dev.sayed.mehrabalmomen.design_system.color

import androidx.compose.ui.graphics.Color

val lightThemeColors = MehrabColors(
    primary = Primary(
        primary = Color(0xFF000000),
        onPrimary = Color(0xFFFFFFFF),
        shadePrimary = Color(0xFF0E1017),
    ), surfaces = Surfaces(
        surface = Color(0xFFF2F4F7),
        surfaceLow = Color(0xFFFFFFFF),
        surfaceHigh = Color(0xFFEAECF0)
    ),
    secondary = Secondary(
        secondary = Color(0xFF66481F),
        shadeSecondary = Color(0xFF3E4252),
        secondaryText = Color(0xFF997D56)
    ),
    semantic = Semantic(
        disabled = Color(0xFFBEC0CC),
        textDisabled = Color(0xFF818599),
        error = Color(0xFFB42318),
        success = Color(0xFF19a44a),
        warning = Color(0xFFDC6803),
        shadeTertiary = Color(0XFF818599)
    )
)