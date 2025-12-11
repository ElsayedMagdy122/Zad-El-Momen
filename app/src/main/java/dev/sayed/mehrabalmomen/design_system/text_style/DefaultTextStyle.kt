package dev.sayed.mehrabalmomen.design_system.text_style

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.R

val poppinsFamily = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semi_bold, FontWeight.SemiBold),
)
val defaultTextStyle = MehrabTextStyle(
    title = TitleTextStyle(
        small = TextStyle(
            fontFamily = poppinsFamily,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        ),
        medium = TextStyle(
            fontFamily = poppinsFamily,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        ),
        large = TextStyle(
            fontFamily = poppinsFamily,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold
        ),
        extraLarge = TextStyle(
            fontFamily = poppinsFamily,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )
    ),
    label = LabelTextStyle(
        small = TextStyle(
            fontFamily = poppinsFamily,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        ),
        medium = TextStyle(
            fontFamily = poppinsFamily,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    ),
    body = BodyTextStyle(
        small = TextStyle(
            fontFamily = poppinsFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        ),
        medium = TextStyle(
            fontFamily = poppinsFamily,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    ),
)