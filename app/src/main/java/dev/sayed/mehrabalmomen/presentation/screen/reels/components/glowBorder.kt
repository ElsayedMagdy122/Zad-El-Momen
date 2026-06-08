package dev.sayed.mehrabalmomen.presentation.screen.reels.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.glowBorder(
    color: Color,
    radius: Dp = 12.dp,
    strokeWidth: Dp = 2.dp,
    glowWidth: Dp = 6.dp
) = this.drawBehind {

    val stroke = strokeWidth.toPx()
    val glow = glowWidth.toPx()

    drawRoundRect(
        color = color,
        size = size,
        cornerRadius = CornerRadius(radius.toPx(), radius.toPx()),
        style = Stroke(width = stroke),
    )

    drawRoundRect(
        color = color.copy(alpha = 0.4f),
        size = size,
        cornerRadius = CornerRadius(radius.toPx(), radius.toPx()),
        style = Stroke(width = stroke + glow),
    )
}