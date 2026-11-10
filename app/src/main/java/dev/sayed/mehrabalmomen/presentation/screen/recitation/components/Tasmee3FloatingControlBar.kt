package dev.sayed.mehrabalmomen.presentation.screen.recitation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme

/**
 * Tasmee3 Control Box designed with the unified TilawahBox aesthetic,
 * featuring standard audio player controls, Tajweed mode toggle, and live ASR feedback.
 */
@Composable
fun Tasmee3FloatingControlBar(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    isRecording: Boolean,
    recognizedText: String,
    lastEvaluatedInfo: String,
    isTajweed: Boolean = false,
    onToggleTajweed: (Boolean) -> Unit = {},
    onToggleRecord: () -> Unit
) {
    Column(
        modifier = modifier
            .widthIn(max = 400.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        // Header in the exact style of TilawahHeader
        Tasmee3Header(isRecording = isRecording)

        // Content in the exact style of TilawahContent
        Tasmee3Content(
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
            text = if (isRecording) "جلسة التسميع الصوتي (نشطة)" else "جلسة التسميع الصوتي",
            color = Theme.color.primary.shadePrimary,
            style = Theme.textStyle.label.small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isRecording) Color(0xFFC62828)
                        else Theme.color.primary.primary
                    )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isRecording) "مستمر" else "جاهز",
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
                    radius = 8.dp,
                    offset = DpOffset(0.dp, -2.dp),
                    color = Color.Black.copy(alpha = 0.06f)
                )
            )
            .background(Theme.color.surfaces.surfaceLow)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Subtitle status line
        Text(
            text = when {
                isLoading -> "جاري تهيئة نموذج الذكاء الاصطناعي..."
                isRecording -> "جاري الاستماع... اقرأ بوضوح"
                else -> "اضغط زر التشغيل للبدء في التسميع"
            },
            color = Theme.color.secondary.shadeSecondary,
            style = Theme.textStyle.label.small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Controls Row
        Row(
            modifier = Modifier.padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // PlayerControls Container with exact Play/Pause audio button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Theme.color.surfaces.surface)
                    .padding(vertical = 4.dp, horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clickable(enabled = !isLoading) { onToggleRecord() }
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val state = when {
                        isLoading -> PlayerState.LOADING
                        isRecording -> PlayerState.PLAYING
                        else -> PlayerState.PAUSED
                    }

                    AnimatedContent(
                        targetState = state,
                        transitionSpec = {
                            (fadeIn(tween(200)) + scaleIn(initialScale = 0.8f))
                                .togetherWith(fadeOut(tween(200)) + scaleOut(targetScale = 0.8f))
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

                            PlayerState.PLAYING -> {
                                Icon(
                                    painter = painterResource(R.drawable.ic_pause),
                                    contentDescription = "Stop",
                                    tint = Theme.color.primary.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            PlayerState.PAUSED -> {
                                Icon(
                                    painter = painterResource(R.drawable.ic_play),
                                    contentDescription = "Play",
                                    tint = Theme.color.primary.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Mic indicator icon
            Icon(
                painter = painterResource(R.drawable.ic_mic),
                contentDescription = null,
                tint = if (isRecording) Color(0xFFC62828) else Theme.color.semantic.shadeTertiary,
                modifier = Modifier.size(20.dp)
            )
        }

        // Live recognized transcript & last evaluated info (if any)
        if (recognizedText.isNotBlank() || lastEvaluatedInfo.isNotBlank()) {
            Spacer(modifier = Modifier.height(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Theme.color.surfaces.surface)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (recognizedText.isNotBlank()) {
                    Text(
                        text = "الصوت المسموع: $recognizedText",
                        style = Theme.textStyle.body.small,
                        color = Theme.color.primary.shadePrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (lastEvaluatedInfo.isNotBlank()) {
                    if (recognizedText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                    Text(
                        text = lastEvaluatedInfo,
                        style = Theme.textStyle.body.small.copy(fontWeight = FontWeight.Bold),
                        color = Theme.color.primary.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

private enum class PlayerState {
    LOADING, PLAYING, PAUSED
}
