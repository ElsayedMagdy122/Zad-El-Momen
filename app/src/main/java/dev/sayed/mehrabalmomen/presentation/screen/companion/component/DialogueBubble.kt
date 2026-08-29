package dev.sayed.mehrabalmomen.presentation.screen.companion.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun DialogueBubble(
    text: String,
    modifier: Modifier = Modifier
) {
    val bubbleColor = Theme.color.surfaces.surfaceLow
    
    // Animation for the "Growing Bubbles" effect
    val animProgress = remember { Animatable(0f) }
    
    LaunchedEffect(text) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        // 1. The Main Message Box
        Box(
            modifier = Modifier
                .graphicsLayer {
                    val scale = Math.max(0f, (animProgress.value - 0.4f) * 1.66f)
                    scaleX = scale
                    scaleY = scale
                    alpha = scale
                }
                .clip(RoundedCornerShape(16.dp))
                .background(bubbleColor)
                .padding(horizontal = 14.dp, vertical = 8.dp), // Tightened vertical padding
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = Theme.textStyle.body.small,
                color = Theme.color.primary.primary,
                textAlign = TextAlign.Center
            )
        }
        
        // 2. The "Growing Bubbles" pointer (Comic style)
        Column(
            modifier = Modifier
                .padding(end = 24.dp) // Adjusted to be closer to center of head
                .width(20.dp)
                .offset(y = (-4).dp), // Negative offset to pull bubbles up into the box slightly
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circle 1 (Largest, closest to box)
            BubbleCircle(bubbleColor, 9.dp, { animProgress.value }, 0.3f)
            Spacer(modifier = Modifier.height(2.dp)) // Reduced spacer height
            // Circle 2 (Medium)
            BubbleCircle(bubbleColor, 5.dp, { animProgress.value }, 0.15f)
            Spacer(modifier = Modifier.height(2.dp)) // Reduced spacer height
            // Circle 3 (Smallest, closest to bird)
            BubbleCircle(bubbleColor, 3.dp, { animProgress.value }, 0f)
        }
    }
}

@Composable
private fun BubbleCircle(
    color: androidx.compose.ui.graphics.Color,
    size: androidx.compose.ui.unit.Dp,
    progressProvider: () -> Float,
    threshold: Float
) {
    Box(
        modifier = Modifier
            .size(size)
            .graphicsLayer {
                val progress = progressProvider()
                val scale = Math.min(1f, Math.max(0f, (progress - threshold) * 5f))
                scaleX = scale
                scaleY = scale
                alpha = scale
            }
            .background(color, CircleShape)
    )
}
