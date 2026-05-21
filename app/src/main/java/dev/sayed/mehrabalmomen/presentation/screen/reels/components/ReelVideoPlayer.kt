package dev.sayed.mehrabalmomen.presentation.screen.reels.components

import android.view.TextureView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay

@Composable
fun ReelVideoPlayer(
    player: ExoPlayer,
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    var isPausedByUser  by remember { mutableStateOf(false) }
    var showPauseIcon   by remember { mutableStateOf(false) }
    var position        by remember { mutableLongStateOf(0L) }

    // ── Keep play/pause in sync with active page ──────────────────────────────
    LaunchedEffect(isActive) {
        if (isActive && !isPausedByUser) {
            player.play()
        } else if (!isActive) {
            player.pause()
        }
    }

    // ── Per-frame position interpolation (only when this reel is on screen) ──
    LaunchedEffect(player, isActive) {
        if (!isActive) return@LaunchedEffect

        var anchorPosition  = player.currentPosition
        var anchorWallClock = System.currentTimeMillis()

        val listener = object : Player.Listener {
            override fun onEvents(p: Player, events: Player.Events) {
                anchorPosition  = p.currentPosition
                anchorWallClock = System.currentTimeMillis()
            }
        }
        player.addListener(listener)

        try {
            while (true) {
                withFrameMillis {
                    if (player.isPlaying) {
                        val elapsed  = System.currentTimeMillis() - anchorWallClock
                        val duration = player.duration.takeIf { it > 0 } ?: Long.MAX_VALUE
                        position = (anchorPosition + elapsed).coerceAtMost(duration)
                    }
                }
            }
        } finally {
            player.removeListener(listener)
        }
    }

    // ── Progress (0f–1f) derived from position ────────────────────────────────
    val progress by remember {
        derivedStateOf {
            val dur = player.duration.takeIf { it > 0 } ?: 1L
            (position.toFloat() / dur).coerceIn(0f, 1f)
        }
    }

    // ── Auto-hide the pause/play icon after 900 ms ────────────────────────────
    LaunchedEffect(showPauseIcon) {
        if (showPauseIcon) {
            delay(900)
            showPauseIcon = false
        }
    }

    // ── UI ────────────────────────────────────────────────────────────────────
    Box(
        modifier = modifier
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication        = null,
            ) {
                if (isActive) {
                    if (player.isPlaying) {
                        player.pause()
                        isPausedByUser = true
                    } else {
                        player.play()
                        isPausedByUser = false
                    }
                    showPauseIcon = true
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        // ── Video surface ─────────────────────────────────────────────────────
        AndroidView(
            factory = { ctx ->
                TextureView(ctx).also { textureView ->
                    player.setVideoTextureView(textureView)
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        // ── Tap feedback: animated pause / play icon ──────────────────────────
        if (isActive) {
            AnimatedVisibility(
                visible = showPauseIcon,
                enter   = fadeIn(),
                exit    = fadeOut(),
            ) {
                PausePlayIcon(isPlaying = player.isPlaying)
            }

            // ── Progress bar pinned above the system navigation bar ───────────
            VideoProgressBar(
                progress = progress,
                onSeek   = { fraction ->
                    val dur    = player.duration.takeIf { it > 0 } ?: return@VideoProgressBar
                    val seekTo = (fraction * dur).toLong()
                    position   = seekTo
                    player.seekTo(seekTo)
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 8.dp),
            )
        }
    }
}
