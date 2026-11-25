package dev.sayed.mehrabalmomen.presentation.screen.qiblah.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.utils.pulsate
import org.jetbrains.compose.resources.painterResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.qibla_compass

@Composable
fun KaabaOnCircle(
    animatedAngle: Float,
    isAligned: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isAligned) Theme.color.semantic.success else Theme.color.secondary.secondaryText,
        animationSpec = tween(durationMillis = 300),
        label = "BorderColorAnimation"
    )

    val scaleValue by animateFloatAsState(
        targetValue = if (isAligned) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "CompassAlignedScale"
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scaleValue
                scaleY = scaleValue
            }
            .clip(CircleShape)
            .border(
                width = 5.dp,
                color = borderColor,
                shape = CircleShape
            )
            .background(Theme.color.surfaces.surface)
            .pulsate(enabled = isAligned, minAlpha = 0.85f, maxAlpha = 1.0f, translationYPx = 2f),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(Res.drawable.qibla_compass),
            contentDescription = null,
            modifier = Modifier
                .padding(6.dp)
                .size(200.dp)
                .graphicsLayer {
                    rotationZ = animatedAngle
                },
            tint = Theme.color.primary.primary
        )
    }
}
