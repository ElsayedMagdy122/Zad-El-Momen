package dev.sayed.mehrabalmomen.design_system.text_style

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font
import zad_el_momen.core.design_system.generated.resources.Res
import zad_el_momen.core.design_system.generated.resources.poppins_medium
import zad_el_momen.core.design_system.generated.resources.poppins_regular
import zad_el_momen.core.design_system.generated.resources.poppins_semi_bold
import zad_el_momen.core.design_system.generated.resources.hafs

@Composable
fun poppinsFontFamily() = FontFamily(
    Font(Res.font.poppins_regular, FontWeight.Normal),
    Font(Res.font.poppins_medium, FontWeight.Medium),
    Font(Res.font.poppins_semi_bold, FontWeight.SemiBold)
)

@Composable
fun hafsFontFamily() = FontFamily(
    Font(Res.font.hafs, FontWeight.Normal)
)

@Composable
fun getDefaultTextStyle(): MehrabTextStyle {
    val poppins = poppinsFontFamily()
    return MehrabTextStyle(
        title = TitleTextStyle(
            small = TextStyle(fontFamily = poppins, fontSize = 16.sp, fontWeight = FontWeight.Medium),
            medium = TextStyle(fontFamily = poppins, fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
            large = TextStyle(fontFamily = poppins, fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
            extraLarge = TextStyle(fontFamily = poppins, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
        ),
        label = LabelTextStyle(
            small = TextStyle(fontFamily = poppins, fontSize = 10.sp, fontWeight = FontWeight.Medium),
            medium = TextStyle(fontFamily = poppins, fontSize = 14.sp, fontWeight = FontWeight.Medium)
        ),
        body = BodyTextStyle(
            small = TextStyle(fontFamily = poppins, fontSize = 12.sp, fontWeight = FontWeight.Medium),
            medium = TextStyle(fontFamily = poppins, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        ),
    )
}
