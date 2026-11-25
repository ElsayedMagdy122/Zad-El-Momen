package dev.sayed.mehrabalmomen.presentation.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Modifier لإضافة تأثير ضغط مرن ونقي (Tactile Bounce) بدون recomposition على أي عنصر UI.
 */
@Composable
fun Modifier.bounceClick(
    enabled: Boolean = true,
    scaleDown: Float = 0.96f,
    onClick: () -> Unit
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animScale = remember { Animatable(1f) }

    LaunchedEffect(isPressed) {
        val targetScale = if (isPressed) scaleDown else 1f
        animScale.animateTo(
            targetValue = targetScale,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    return this
        .graphicsLayer {
            scaleX = animScale.value
            scaleY = animScale.value
        }
        .clickable(
            enabled = enabled,
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick
        )
}

/**
 * Modifier لإضافة أنيميشن نبض عمودي وتدرج الشفافية اللطيفة للمؤشرات والشارات.
 */
@Composable
fun Modifier.pulsate(
    enabled: Boolean = true,
    minAlpha: Float = 0.5f,
    maxAlpha: Float = 1.0f,
    translationYPx: Float = 4f,
    durationMillis: Int = 1200
): Modifier {
    if (!enabled) return this

    val infiniteTransition = rememberInfiniteTransition(label = "PulsateTransition")
    val alpha by infiniteTransition.animateFloat(
        initialValue = minAlpha,
        targetValue = maxAlpha,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulsateAlpha"
    )
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = translationYPx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulsateOffset"
    )

    return this.graphicsLayer {
        this.alpha = alpha
        this.translationY = offsetY
    }
}
