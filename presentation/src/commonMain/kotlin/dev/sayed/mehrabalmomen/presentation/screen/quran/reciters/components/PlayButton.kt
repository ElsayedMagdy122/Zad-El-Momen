package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.PlayState

@Composable
fun PlayButton(
    playState: PlayState,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Theme.color.surfaces.surfaceHigh,
    iconTint: Color = Theme.color.primary.primary
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable(enabled = playState != PlayState.LOADING, onClick = onPlayClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = playState,
            animationSpec = tween(durationMillis = 200),
            label = "PlayButtonCrossfade"
        ) { state ->
            when (state) {
                PlayState.LOADING -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp),
                        color = iconTint,
                        strokeWidth = 2.5.dp
                    )
                }

                PlayState.PLAY -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_pause),
                        contentDescription = "Pause Audio",
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }

                PlayState.RESUME -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_play),
                        contentDescription = "Play Audio",
                        tint = iconTint,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}


@Preview
@Composable
private fun PlayButtonPreview() {
    MehrabTheme() {
        PlayButton(
            playState = PlayState.RESUME,
            onPlayClick = {}
        )
    }
}