package dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun SectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = Theme.color.primary.shadePrimary,
    style: TextStyle = Theme.textStyle.title.medium,
) {
    Text(
        modifier = modifier,
        text = text,
        style = style,
        color = textColor
    )
}