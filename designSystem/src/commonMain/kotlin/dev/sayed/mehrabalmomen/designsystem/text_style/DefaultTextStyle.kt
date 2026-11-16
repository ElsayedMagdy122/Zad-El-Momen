package dev.sayed.mehrabalmomen.design_system.text_style

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import dev.sayed.mehrabalmomen.designsystem.generated.resources.Res
import dev.sayed.mehrabalmomen.designsystem.generated.resources.hafs
import dev.sayed.mehrabalmomen.designsystem.generated.resources.poppins_medium
import dev.sayed.mehrabalmomen.designsystem.generated.resources.poppins_regular
import dev.sayed.mehrabalmomen.designsystem.generated.resources.poppins_semi_bold

val poppinsFamily: FontFamily
    @Composable
    get() = FontFamily(
        Font(Res.font.poppins_regular, FontWeight.Normal),
        Font(Res.font.poppins_medium, FontWeight.Medium),
        Font(Res.font.poppins_semi_bold, FontWeight.SemiBold),
    )

val hafsFamily: FontFamily
    @Composable
    get() = FontFamily(
        Font(Res.font.hafs, FontWeight.Normal)
    )

val defaultTextStyle: MehrabTextStyle
    @Composable
    get() = MehrabTextStyle(
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
        )
    )
