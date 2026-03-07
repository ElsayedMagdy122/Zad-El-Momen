package dev.sayed.mehrabalmomen.presentation.screen.radio.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
            Icon(
                painter = painterResource(mediaPlayerIcon),
                tint = Theme.color.primary.primary,
                contentDescription = ""
            )
        }
        Text(
            text = reciterName,
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
