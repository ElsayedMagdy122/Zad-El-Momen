package dev.sayed.mehrabalmomen.presentation.screen.reels.components
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

import androidx.compose.ui.graphics.graphicsLayer
fun Modifier.clickAnimation(enabled : Boolean = true, onClick: () -> Unit): Modifier = composed {
    var animateTrigger by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (animateTrigger) 1.1f else 1f,
        animationSpec = tween(120),
        finishedListener = {
            animateTrigger = false
        }
    )

    this.graphicsLayer {
        this.scaleX = scale
        this.scaleY = scale
    }
        .clickable(
            indication = null,
            enabled = enabled,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            animateTrigger = true
            onClick()
        }
}