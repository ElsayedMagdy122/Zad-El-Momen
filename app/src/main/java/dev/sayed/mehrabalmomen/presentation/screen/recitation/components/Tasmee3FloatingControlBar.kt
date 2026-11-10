package dev.sayed.mehrabalmomen.presentation.screen.recitation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString

/**
 * Tasmee3 Control Box designed with the unified TilawahBox aesthetic,
 * featuring standard audio player controls, Tajweed mode toggle, and live ASR feedback.
 * Enhanced with subtle, fluid micro-animations (pulsing recording indicator, text sliding, bouncy button).
 */
@Composable
fun Tasmee3FloatingControlBar(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    isRecording: Boolean,
    recognizedText: String = "",
    lastEvaluatedInfo: String = "",
    isTajweed: Boolean = false,
    onToggleTajweed: (Boolean) -> Unit = {},
    onToggleRecord: () -> Unit
) {
    Column(
        modifier = modifier
            .widthIn(max = 400.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        // Header with pulsing recording status
        Tasmee3Header(isRecording = isRecording)

        // Content with animated subtitle and mic button
        Tasmee3Content(
            isLoading = isLoading,
            isRecording = isRecording,
            onToggleRecord = onToggleRecord
        )
    }
}

@Composable
private fun Tasmee3Header(
    isRecording: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.color.surfaces.surfaceLow)
            .padding(vertical = 6.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isRecording) localizedString(R.string.tasmee3_session_active) else localizedString(R.string.tasmee3_session_title),
            color = Theme.color.primary.shadePrimary,
            style = Theme.textStyle.label.small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isRecording) {
                val infiniteTransition = rememberInfiniteTransition(label = "pulseTransition")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.7f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "pulseScale"
                )
                val pulseAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.5f,
                    targetValue = 0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Restart
                    ),
                    label = "pulseAlpha"
                )

                Box(
                    modifier = Modifier.size(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer expanding ripple ring using draw-phase graphicsLayer for zero recomposition
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                                alpha = pulseAlpha
                            }
                            .clip(CircleShape)
                            .background(Color(0xFFC62828))
                    )
                    // Inner solid core dot
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFC62828))
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Theme.color.primary.primary)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isRecording) localizedString(R.string.tasmee3_status_ongoing) else localizedString(R.string.tasmee3_status_ready),
                color = if (isRecording) Color(0xFFC62828) else Theme.color.semantic.shadeTertiary,
                style = Theme.textStyle.body.small
            )
        }
    }
}

@Composable
private fun Tasmee3Content(
    isLoading: Boolean,
    isRecording: Boolean,
    onToggleRecord: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RectangleShape,
                shadow = Shadow(
                    radius = 12.dp,
                    offset = DpOffset(0.dp, 4.dp),
                    color = Color.Black.copy(alpha = 0.04f)
                )
            )
            .dropShadow(
                shape = RectangleShape,
                shadow = Shadow(
                    radius = 8.dp,
                    offset = DpOffset(0.dp, -2.dp),
                    color = Color.Black.copy(alpha = 0.06f)
                )
            )
            .background(Theme.color.surfaces.surfaceLow)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Subtitle status line with smooth slide-and-fade animation
        val statusText = when {
            isLoading -> localizedString(R.string.tasmee3_status_initializing)
            isRecording -> localizedString(R.string.tasmee3_status_listening)
            else -> localizedString(R.string.tasmee3_status_prompt)
        }

        AnimatedContent(
            targetState = statusText,
            transitionSpec = {
                slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(250, easing = FastOutSlowInEasing)
                ) + fadeIn(tween(200)) togetherWith
                slideOutVertically(
                    targetOffsetY = { -it / 2 },
                    animationSpec = tween(200)
                ) + fadeOut(tween(150))
            },
            label = "statusTextAnim"
        ) { text ->
            Text(
                text = text,
                color = Theme.color.secondary.shadeSecondary,
                style = Theme.textStyle.label.small,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Controls Row with authentic Microphone button
        Row(
            modifier = Modifier.padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val buttonScale by animateFloatAsState(
                targetValue = if (isPressed) 0.90f else 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "micButtonScale"
            )

            Row(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = buttonScale
                        scaleY = buttonScale
                    }
                    .clip(RoundedCornerShape(8.dp))
                    .background(Theme.color.surfaces.surface)
                    .padding(vertical = 4.dp, horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            enabled = !isLoading
                        ) { onToggleRecord() }
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val state = when {
                        isLoading -> PlayerState.LOADING
                        isRecording -> PlayerState.RECORDING
                        else -> PlayerState.IDLE
                    }

                    AnimatedContent(
                        targetState = state,
                        transitionSpec = {
                            (fadeIn(tween(200)) + scaleIn(
                                initialScale = 0.8f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            )).togetherWith(
                                fadeOut(tween(200)) + scaleOut(targetScale = 0.8f)
                            )
                        },
                        label = "Tasmee3PlayerStateAnimation"
                    ) { targetState ->
                        when (targetState) {
                            PlayerState.LOADING -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Theme.color.primary.primary,
                                    strokeWidth = 2.5.dp
                                )
                            }

                            PlayerState.RECORDING -> {
                                Icon(
                                    painter = painterResource(R.drawable.ic_mic),
                                    contentDescription = null,
                                    tint = Color(0xFFC62828),
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            PlayerState.IDLE -> {
                                Icon(
                                    painter = painterResource(R.drawable.ic_mic),
                                    contentDescription = null,
                                    tint = Theme.color.primary.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private enum class PlayerState {
    LOADING,
    RECORDING,
    IDLE
}
