package dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.random.Random

@Composable
fun LeafBranch(
    modifier: Modifier = Modifier,
    leafColor: Color = Color(0xFFC5E1A5)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "leaves")
    
    // Sway animation for the whole branch or individual leaves
    val sway by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sway"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // Draw the main branch line (stem)
        val stemPath = Path().apply {
            moveTo(width * 0.8f, height * 0.9f)
            quadraticTo(width * 0.7f, height * 0.5f, width * 0.85f, height * 0.1f)
        }
        drawPath(stemPath, Color(0xFF8D6E63), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f))

        // Draw leaves along the stem
        val leafPoints = listOf(
            Offset(width * 0.78f, height * 0.75f) to -40f,
            Offset(width * 0.74f, height * 0.65f) to 30f,
            Offset(width * 0.76f, height * 0.5f) to -35f,
            Offset(width * 0.78f, height * 0.35f) to 25f,
            Offset(width * 0.82f, height * 0.2f) to -20f
        )

        leafPoints.forEachIndexed { index, (pos, baseRotation) ->
            val individualSway = sway * (1 + (index * 0.2f))
            withTransform({
                rotate(baseRotation + individualSway, pos)
            }) {
                val leafPath = Path().apply {
                    moveTo(pos.x, pos.y)
                    quadraticTo(pos.x - 20f, pos.y - 40f, pos.x, pos.y - 80f)
                    quadraticTo(pos.x + 20f, pos.y - 40f, pos.x, pos.y)
                }
                drawPath(leafPath, leafColor)
                // Add a slightly darker edge for depth
                drawPath(leafPath, leafColor.copy(alpha = 0.3f), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f))
            }
        }
    }
}
