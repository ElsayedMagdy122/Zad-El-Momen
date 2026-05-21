package dev.sayed.mehrabalmomen.presentation.screen.reels.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun PausePlayIcon(
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(Theme.color.primary.onPrimaryHint)
        ,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            ),
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = Theme.color.primary.onPrimary,
            modifier = Modifier.size(20.dp),
        )
    }
}
