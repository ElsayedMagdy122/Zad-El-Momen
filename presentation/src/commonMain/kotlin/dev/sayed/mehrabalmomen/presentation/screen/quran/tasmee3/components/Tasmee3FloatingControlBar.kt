package dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.components

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.ic_mic
import dev.sayed.mehrabalmomen.designsystem.resources.tasmee3_session_active
import dev.sayed.mehrabalmomen.designsystem.resources.tasmee3_session_title
import dev.sayed.mehrabalmomen.designsystem.resources.tasmee3_status_initializing
import dev.sayed.mehrabalmomen.designsystem.resources.tasmee3_status_listening
import dev.sayed.mehrabalmomen.designsystem.resources.tasmee3_status_ongoing
import dev.sayed.mehrabalmomen.designsystem.resources.tasmee3_status_prompt
import dev.sayed.mehrabalmomen.designsystem.resources.tasmee3_status_ready
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import org.jetbrains.compose.resources.painterResource

/**
 * Tasmee3 Control Box designed with the unified TilawahBox aesthetic,
 * featuring a dual-layer container (Header + Content Card with refined shadows and subtle borders).
 */
@Composable
fun Tasmee3FloatingControlBar(
    isLoading: Boolean,
    isRecording: Boolean,
    recognizedText: String,
    lastEvaluatedInfo: String = "",
    onToggleRecord: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {
        Tasmee3Header(
            isRecording = isRecording,
            modifier = Modifier.padding(bottom = 2.dp)
        )

        Tasmee3ContentCard(
            isLoading = isLoading,
            isRecording = isRecording,
            recognizedText = recognizedText,
            lastEvaluatedInfo = lastEvaluatedInfo,
            onToggleRecord = onToggleRecord
        )
    }
}

@Composable
private fun Tasmee3Header(
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.color.surfaces.surfaceLow)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isRecording) localizedString(Res.string.tasmee3_session_active) else localizedString(Res.string.tasmee3_session_title),
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        // Pulse dot when active
        if (isRecording) {
            val infiniteTransition = rememberInfiniteTransition(label = "HeaderPulseTransition")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.35f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(650, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "HeaderPulseAlpha"
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE53935).copy(alpha = pulseAlpha))
            )

            Spacer(modifier = Modifier.width(6.dp))
        }

        Text(
            text = if (isRecording) localizedString(Res.string.tasmee3_status_ongoing) else localizedString(Res.string.tasmee3_status_ready),
            style = Theme.textStyle.label.small,
            color = if (isRecording) Color(0xFFC62828) else Theme.color.semantic.shadeTertiary,
            maxLines = 1
        )
    }
}

@Composable
private fun Tasmee3ContentCard(
    isLoading: Boolean,
    isRecording: Boolean,
    recognizedText: String,
    lastEvaluatedInfo: String,
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
                    radius = 4.dp,
                    offset = DpOffset(0.dp, (-2).dp),
                    color = Color.Black.copy(alpha = 0.06f)
                )
            )
            .background(Theme.color.surfaces.surfaceLow)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val statusMessage = when {
            isLoading -> localizedString(Res.string.tasmee3_status_initializing)
            isRecording -> localizedString(Res.string.tasmee3_status_listening)
            else -> localizedString(Res.string.tasmee3_status_prompt)
        }

        Text(
            text = statusMessage,
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Tasmee3ActionButton(
                isLoading = isLoading,
                isRecording = isRecording,
                onClick = onToggleRecord
            )
        }
    }
}

@Composable
private fun Tasmee3ActionButton(
    isLoading: Boolean,
    isRecording: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "ActionButtonScale"
    )

    Box(
        modifier = Modifier
            .size(54.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surface)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = isLoading to isRecording,
            transitionSpec = {
                (fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.8f)).togetherWith(
                    fadeOut(animationSpec = tween(110)) + scaleOut(targetScale = 0.8f)
                )
            },
            label = "Tasmee3ButtonAnimation"
        ) { (loading, recording) ->
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Theme.color.primary.primary,
                    strokeWidth = 2.dp
                )
            } else {
                if (recording) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_mic),
                        contentDescription = "Stop Tasmee3",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.ic_mic),
                        contentDescription = "Start Tasmee3",
                        tint = Theme.color.primary.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
