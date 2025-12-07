package dev.sayed.mehrabalmomen.design_system.text_style

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

data class MehrabTextStyle(
    val title: TitleTextStyle,
    val label: LabelTextStyle,
    val body: BodyTextStyle,
)
data class TitleTextStyle(
    val small: TextStyle,
    val medium: TextStyle,
    val large: TextStyle,
    val extraLarge: TextStyle
)

data class LabelTextStyle(
    val small: TextStyle,
    val medium: TextStyle,
)

data class BodyTextStyle(
    val small: TextStyle,
    val medium: TextStyle,
)



internal val LocalMehrabTextStyle = staticCompositionLocalOf { defaultTextStyle }
