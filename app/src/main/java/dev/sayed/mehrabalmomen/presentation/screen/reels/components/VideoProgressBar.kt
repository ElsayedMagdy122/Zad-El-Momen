package dev.sayed.mehrabalmomen.presentation.screen.reels.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme

private val TRACK_HEIGHT      = 3.dp
private val TRACK_HEIGHT_DRAG = 5.dp

@Composable
fun VideoProgressBar(
    progress: Float,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    var trackWidthPx by remember { mutableFloatStateOf(0f) }
    var isDragging   by remember { mutableStateOf(false) }
    var dragProgress by remember { mutableFloatStateOf(0f) }

    val displayProgress = if (isDragging) dragProgress else progress

    val trackHeight by animateDpAsState(
        targetValue    = if (isDragging) TRACK_HEIGHT_DRAG else TRACK_HEIGHT,
        animationSpec  = tween(150),
        label          = "trackHeight",
    )

    // Force LTR so progress always grows left-to-right regardless of app locale
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Box(modifier = modifier.padding(horizontal = 16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .offset(y = 24.dp)
                    .onGloballyPositioned { trackWidthPx = it.size.width.toFloat() }
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val down     = awaitFirstDown(requireUnconsumed = false)
                            val fraction = (down.position.x / trackWidthPx).coerceIn(0f, 1f)

                            isDragging   = true
                            dragProgress = fraction

                            do {
                                val event  = awaitPointerEvent()
                                val change = event.changes.firstOrNull() ?: break
                                if (change.positionChanged()) {
                                    dragProgress = (change.position.x / trackWidthPx).coerceIn(0f, 1f)
                                    change.consume()
                                }
                            } while (event.changes.any { it.pressed })

                            onSeek(dragProgress)
                            isDragging = false
                        }
                    },
            ) {
                // ── Track background ──────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .height(trackHeight)
                        .offset(y = -24.dp)
                        .background(
                            color = Theme.color.primary.primary.copy(alpha = 0.30f),
                            shape = RoundedCornerShape(50),
                        ),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    // ── Filled portion ────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(displayProgress)
                            .height(trackHeight)
                            .border(
                                width  = trackHeight / 2,
                                color  = Theme.color.brand.brand,
                                shape  = RoundedCornerShape(
                                    topEndPercent    = 50,
                                    bottomEndPercent = 50,
                                ),
                            )
                            .background(
                                color = Theme.color.brand.brand,
                                shape = RoundedCornerShape(50),
                            ).glowBorder(Theme.color.brand.brand),
                    )
                }
            }
        }
    }
}
