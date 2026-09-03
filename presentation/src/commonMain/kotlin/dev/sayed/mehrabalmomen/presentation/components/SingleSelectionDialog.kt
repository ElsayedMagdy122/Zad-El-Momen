package dev.sayed.mehrabalmomen.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.LocalIsDarkTheme
import dev.sayed.mehrabalmomen.presentation.utils.DialogWindowStyle

@Composable
fun PrimaryDialog(
    onDismiss: () -> Unit,
    backgroundColor: Color = Theme.color.surfaces.surfaceHigh,
    content: @Composable ColumnScope.() -> Unit
) {
    val language = LocalAppLocale.current
    val isDark = LocalIsDarkTheme.current
    
    Dialog(
        onDismissRequest = onDismiss
    ) {
        MehrabTheme(
            language = language,
            isDarkTheme = isDark
        ) {
            DialogWindowStyle()

            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(backgroundColor)
                    .widthIn(max = 400.dp)
            ) {
                content()
            }
        }
    }
}
