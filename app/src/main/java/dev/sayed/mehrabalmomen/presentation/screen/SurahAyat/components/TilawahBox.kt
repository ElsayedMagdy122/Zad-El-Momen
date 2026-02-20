package dev.sayed.mehrabalmomen.presentation.screen.SurahAyat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun TilawahBox1(modifier: Modifier = Modifier) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Column(
        modifier = modifier
            .widthIn(max = 400.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Theme.color.surfaces.surfaceLow)
                .padding(vertical = 6.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Maytham Al-Tammar",
                color = Theme.color.primary.shadePrimary,
                style = Theme.textStyle.label.small,
                textAlign = TextAlign.Start,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(R.drawable.ic_close),
                tint = Theme.color.primary.primary,
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // radius = blur in figma
                .dropShadow(
                    shape = RectangleShape, shadow = Shadow(
                        radius = 12.dp,
                        offset = DpOffset(x = 0.dp, y = 4.dp),
                        color = Color.Black.copy(alpha = 0.04f),
                    )
                )
                .dropShadow(
                    shape = RectangleShape, shadow = Shadow(
                        radius = 8.dp,
                        offset = DpOffset(x = 0.dp, y = -2.dp),
                        color = Color.Black.copy(alpha = 0.06f),
                    )
                )
                .background(Theme.color.surfaces.surfaceLow)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Al-Baarah: 4 aya",
                color = Theme.color.secondary.shadeSecondary,
                style = Theme.textStyle.label.small,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_user_list),
                    contentDescription = null,
                    tint = Theme.color.primary.primary
                )
                Row(
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Theme.color.surfaces.surface)
                        .padding(vertical = 4.dp, horizontal = 12.dp),
                ) {
                    Icon(
                        modifier = Modifier.graphicsLayer {
                            scaleX = if (isRtl) -1f else 1f
                        },
                        painter = painterResource(R.drawable.ic_backward_02),
                        contentDescription = null,
                        tint = Theme.color.primary.primary
                    )
                    Icon(
                        modifier = Modifier.padding(horizontal = 32.dp),
                        painter = painterResource(R.drawable.ic_pause),
                        contentDescription = null,
                        tint = Theme.color.primary.primary
                    )
                    Icon(
                        modifier = Modifier.graphicsLayer {
                            scaleX = if (isRtl) -1f else 1f
                        },
                        painter = painterResource(R.drawable.ic_forward_02),
                        contentDescription = null,
                        tint = Theme.color.primary.primary
                    )
                }
                Icon(
                    painter = painterResource(R.drawable.ic_repeat_one_01),
                    contentDescription = null,
                    tint = Theme.color.primary.primary
                )
                Icon(
                    modifier = Modifier.graphicsLayer {
                        scaleX = if (isRtl) -1f else 1f
                    },
                    painter = painterResource(R.drawable.ic_curvy_right_direction),
                    contentDescription = null,
                    tint = Theme.color.primary.primary
                )
            }
        }
    }

}
@Composable
fun TilawahBox(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit = {},
    onRecitersClick: () -> Unit = {},
    onBackwardClick: () -> Unit = {},
    onPlayPauseClick: () -> Unit = {},
    onForwardClick: () -> Unit = {},
    onRepeatClick: () -> Unit = {},
    onPlaylistClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .widthIn(max = 400.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        TilawahHeader(
            onCloseClick = onCloseClick
        )

        TilawahContent(
            isPlaying = isPlaying,
            onRecitersClick = onRecitersClick,
            onBackwardClick = onBackwardClick,
            onPlayPauseClick = onPlayPauseClick,
            onForwardClick = onForwardClick,
            onRepeatClick = onRepeatClick,
            onPlaylistClick = onPlaylistClick,
        )
    }
}
@Composable
private fun TilawahHeader(
    onCloseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.color.surfaces.surfaceLow)
            .padding(vertical = 6.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Maytham Al-Tammar",
            color = Theme.color.primary.shadePrimary,
            style = Theme.textStyle.label.small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = null,
            tint = Theme.color.primary.primary,
            modifier = Modifier.clickable { onCloseClick() }
        )
    }
}
@Composable
private fun TilawahContent(
    isPlaying: Boolean,
    onRecitersClick: () -> Unit,
    onBackwardClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onForwardClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onPlaylistClick: () -> Unit,
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
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "Al-Baarah: 4 aya",
            color = Theme.color.secondary.shadeSecondary,
            style = Theme.textStyle.label.small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        ControlsRow(
            isPlaying = isPlaying,
            onRecitersClick,
            onBackwardClick,
            onPlayPauseClick,
            onForwardClick,
            onRepeatClick,
            onPlaylistClick
        )
    }
}
@Composable
private fun ControlsRow(
    isPlaying: Boolean,
    onRecitersClick: () -> Unit,
    onBackwardClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onForwardClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onPlaylistClick: () -> Unit,
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    Row(
        modifier = Modifier.padding(top = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        Icon(
            painter = painterResource(R.drawable.ic_user_list),
            contentDescription = null,
            tint = Theme.color.primary.primary,
            modifier = Modifier.clickable { onRecitersClick() }
        )

        PlayerControls(
            isRtl = isRtl,
            isPlaying = isPlaying,
            onBackwardClick = onBackwardClick,
            onPlayPauseClick = onPlayPauseClick,
            onForwardClick = onForwardClick
        )

        Icon(
            painter = painterResource(R.drawable.ic_repeat_one_01),
            contentDescription = null,
            tint = Theme.color.primary.primary,
            modifier = Modifier.clickable { onRepeatClick() }
        )

        Icon(
            modifier = Modifier
                .graphicsLayer { scaleX = if (isRtl) -1f else 1f }
                .clickable { onPlaylistClick() },
            painter = painterResource(R.drawable.ic_curvy_right_direction),
            contentDescription = null,
            tint = Theme.color.primary.primary
        )
    }
}
@Composable
private fun PlayerControls(
    isRtl: Boolean,
    isPlaying: Boolean,
    onBackwardClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onForwardClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = 12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Theme.color.surfaces.surface)
            .padding(vertical = 4.dp, horizontal = 12.dp),
    ) {

        Icon(
            modifier = Modifier
                .graphicsLayer { scaleX = if (isRtl) -1f else 1f }
                .clickable { onBackwardClick() },
            painter = painterResource(R.drawable.ic_backward_02),
            contentDescription = null,
            tint = Theme.color.primary.primary
        )

        Icon(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .clickable { onPlayPauseClick() },
            painter = painterResource(
                if (isPlaying)
                    R.drawable.ic_pause
                else
                    R.drawable.ic_play
            ),
            contentDescription = null,
            tint = Theme.color.primary.primary
        )

        Icon(
            modifier = Modifier
                .graphicsLayer { scaleX = if (isRtl) -1f else 1f }
                .clickable { onForwardClick() },
            painter = painterResource(R.drawable.ic_forward_02),
            contentDescription = null,
            tint = Theme.color.primary.primary
        )
    }
}
@Preview
@Composable
private fun TilawahBoxPreview() {
    MehrabTheme(isDarkTheme = false) {
        TilawahBox(isPlaying = false)
    }
}