package dev.sayed.mehrabalmomen.presentation.screen.radio.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioChannelsInteractionListener
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioUiState

@Composable
fun RadioReciterItem(
    state: RadioUiState.RadioChannelUiState,
    listener: RadioChannelsInteractionListener,
    modifier: Modifier = Modifier
) {
    val mediaPlayerIcon = if (state.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val reciterName = if (isRtl) state.nameAr else state.nameEn
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (state.selected) Modifier.border(
                    width = 1.dp,
                    color = Theme.color.primary.primary,
                    shape = RoundedCornerShape(12.dp)
                )
                else Modifier
            )
            .background(Theme.color.surfaces.surfaceLow)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Theme.color.surfaces.surfaceHigh)
                .clickable {
                    if (state.isPlaying) {
                        listener.onPauseClick(state.id)
                    } else {
                        listener.onPlayClick(state.id)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp),
                        color = Theme.color.primary.primary
                    )
                }

                else -> {
                    Icon(
                        painter = painterResource(mediaPlayerIcon),
                        tint = Theme.color.primary.primary,
                        contentDescription = ""
                    )
                }
            }
        }
        Text(
            text = reciterName,
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary,
            modifier = Modifier.padding(start = 8.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        AnimatedVisibility(
            modifier = Modifier
                .width(50.dp)
                .height(20.dp)
                .padding(horizontal = 8.dp),
            visible = state.isPlaying,
            enter = fadeIn(),
            exit = fadeOut()
        ) {

            EqualizerBars(
                isPlaying = state.isPlaying
            )
        }
    }
}

@Composable
private fun EqualizerBars(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barsCount: Int = 15
) {
    val infiniteTransition = rememberInfiniteTransition(label = "")
    val color = Theme.color.primary.primary
    val animations = List(barsCount) {
        infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = (300..800).random(),
                    easing = LinearEasing
                ),
                repeatMode = RepeatMode.Reverse
            ),
            label = ""
        )
    }

    Canvas(modifier = modifier) {
        val barWidth = size.width / (barsCount * 2)

        animations.forEachIndexed { index, anim ->
            val value = if (isPlaying) anim.value else 0.1f
            val barHeight = value * size.height

            drawRect(
                color = color,
                topLeft = Offset(
                    x = index * barWidth * 2,
                    y = size.height - barHeight
                ),
                size = Size(barWidth, barHeight)
            )
        }
    }
}
