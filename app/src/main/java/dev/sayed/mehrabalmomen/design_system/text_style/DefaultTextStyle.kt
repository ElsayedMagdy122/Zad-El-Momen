package dev.sayed.mehrabalmomen.design_system.text_style

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val defaultTextStyle = MehrabTextStyle(
    title = TitleTextStyle(
        small = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        ),
        medium = TextStyle(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        ),
        large = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        ),
        extraLarge = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )
    ),
    label = LabelTextStyle(
        small = TextStyle(
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        ),
        medium = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    ),
    body = BodyTextStyle(
        small = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        ),
        medium = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    ),
)