package dev.sayed.mehrabalmomen.presentation.screen.ReportBug.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.dashedBorder(
    width: Dp,
    color: Color,
    shape: Shape,
    dashOn: Dp = 8.dp,
    dashOff: Dp = 6.dp
) = composed {
    this
        .clip(shape)
        .drawWithContent {
            drawContent()
            val strokeWidthPx = width.toPx()
            val stroke = Stroke(
                width = strokeWidthPx,
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(dashOn.toPx(), dashOff.toPx()),
                    phase = 0f
                )
            )
            val cornerRadiusPx = when (shape) {
                is RoundedCornerShape -> {
                    val density = this
                    val size = this.size
                    maxOf(
                        shape.topStart.toPx(size, density),
                        shape.topEnd.toPx(size, density),
                        shape.bottomStart.toPx(size, density),
                        shape.bottomEnd.toPx(size, density)
                    )
                }
                else -> 0f
            }
            drawRoundRect(
                color = color,
                topLeft = Offset.Zero,
                size = size,
                cornerRadius = CornerRadius(cornerRadiusPx),
                style = stroke
            )
        }
}
