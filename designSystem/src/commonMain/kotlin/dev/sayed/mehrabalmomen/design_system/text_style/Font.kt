package dev.sayed.mehrabalmomen.design_system.text_style

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.hafs
import dev.sayed.mehrabalmomen.designsystem.resources.poppins_regular
import dev.sayed.mehrabalmomen.designsystem.resources.poppins_medium
import dev.sayed.mehrabalmomen.designsystem.resources.poppins_semi_bold

val Poppins: FontFamily
    @Composable
    get() = FontFamily(
        Font(Res.font.poppins_regular, FontWeight.Normal),
        Font(Res.font.poppins_medium, FontWeight.Medium),
        Font(Res.font.poppins_semi_bold, FontWeight.SemiBold)
    )

val Hafs: FontFamily
    @Composable
    get() = FontFamily(
        Font(Res.font.hafs, FontWeight.Normal)
    )
