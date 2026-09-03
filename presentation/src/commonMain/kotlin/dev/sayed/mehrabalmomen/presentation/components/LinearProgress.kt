package dev.sayed.mehrabalmomen.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun LinearProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 5.dp,
    colorCompleted: Color = Theme.color.secondary.secondaryText,
    colorRemaining: Color = Color.White.copy(alpha = 0.20f)
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(CircleShape)
            .background(colorRemaining)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .clip(RoundedCornerShape(height / 2))
                .background(colorCompleted)
        )
    }
}

@Preview
@Composable
private fun LinearProgressPreview() {
    LinearProgress(progress = 0.65f)
}