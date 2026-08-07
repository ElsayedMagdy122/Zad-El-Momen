package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits

@Composable
fun TilawahBox(
    readerName: String,
    surahName: String,
    currentAyahNumber: Int,
    isPlaying: Boolean,
    isLoading: Boolean,
    repeatCount: Int,
    isContinuous: Boolean,
    modifier: Modifier = Modifier,
    onCloseClick: () -> Unit = {},
    onRecitersClick: () -> Unit = {},
    onBackwardClick: () -> Unit = {},
    onPlayPauseClick: () -> Unit = {},
    onForwardClick: () -> Unit = {},
    onRepeatClick: () -> Unit = {},
    onContinuousClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .widthIn(max = 400.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        TilawahHeader(
            readerName = readerName,
            onCloseClick = onCloseClick
        )

        TilawahContent(
            surahName = surahName,
            currentAyahNumber = currentAyahNumber,
            isPlaying = isPlaying,
            isLoading = isLoading,
            repeatCount = repeatCount,
            isContinuous = isContinuous,
            onRecitersClick = onRecitersClick,
            onBackwardClick = onBackwardClick,
            onPlayPauseClick = onPlayPauseClick,
            onForwardClick = onForwardClick,
            onRepeatClick = onRepeatClick,
            onContinuousClick = onContinuousClick,
        )
    }
}

@Composable
private fun TilawahHeader(
    readerName: String,
    onCloseClick: () -> Unit
) {
    val displayName = readerName.ifBlank { localizedString(R.string.select_reciter) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Theme.color.surfaces.surfaceLow)
            .padding(vertical = 6.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = displayName,
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
    surahName: String,
    currentAyahNumber: Int,
    isPlaying: Boolean,
    isLoading: Boolean,
    repeatCount: Int,
    isContinuous: Boolean,
    onRecitersClick: () -> Unit,
    onBackwardClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onForwardClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onContinuousClick: () -> Unit,
) {
    val appLocale = LocalAppLocale.current
    val formattedAyahText = localizedString(
        id = R.string.surah_ayah_format,
        surahName,
        currentAyahNumber.toString().toLocalizedDigits(appLocale)
    )

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
            text = formattedAyahText,
            color = Theme.color.secondary.shadeSecondary,
            style = Theme.textStyle.label.small,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        ControlsRow(
            isPlaying = isPlaying,
            isLoading = isLoading,
            repeatCount = repeatCount,
            isContinuous = isContinuous,
            onRecitersClick = onRecitersClick,
            onBackwardClick = onBackwardClick,
            onPlayPauseClick = onPlayPauseClick,
            onForwardClick = onForwardClick,
            onRepeatClick = onRepeatClick,
            onContinuousClick = onContinuousClick
        )
    }
}

@Composable
private fun ControlsRow(
    isPlaying: Boolean,
    isLoading: Boolean,
    repeatCount: Int,
    isContinuous: Boolean,
    onRecitersClick: () -> Unit,
    onBackwardClick: () -> Unit,
    onPlayPauseClick: () -> Unit,
    onForwardClick: () -> Unit,
    onRepeatClick: () -> Unit,
    onContinuousClick: () -> Unit,
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
            isLoading = isLoading,
            onBackwardClick = onBackwardClick,
            onPlayPauseClick = onPlayPauseClick,
            onForwardClick = onForwardClick
        )

        Icon(
            painter = painterResource(R.drawable.ic_repeat_one_01),
            contentDescription = null,
            tint = if (repeatCount > 0) Theme.color.primary.primary else Theme.color.semantic.shadeTertiary,
            modifier = Modifier.clickable { onRepeatClick() }
        )

        Icon(
            modifier = Modifier
                .graphicsLayer { scaleX = if (isRtl) -1f else 1f }
                .clickable { onContinuousClick() },
            painter = painterResource(R.drawable.ic_curvy_right_direction),
            contentDescription = null,
            tint = if (isContinuous) Theme.color.primary.primary else Theme.color.semantic.shadeTertiary
        )
    }
}

@Composable
private fun PlayerControls(
    isRtl: Boolean,
    isPlaying: Boolean,
    isLoading: Boolean,
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .graphicsLayer { scaleX = if (isRtl) -1f else 1f }
                .clickable { onBackwardClick() },
            painter = painterResource(R.drawable.ic_backward_02),
            contentDescription = null,
            tint = Theme.color.primary.primary
        )

        Box(
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .clickable(enabled = !isLoading) { onPlayPauseClick() },
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Theme.color.primary.primary,
                    strokeWidth = 2.5.dp
                )
            } else {
                Icon(
                    painter = painterResource(
                        if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
                    ),
                    contentDescription = null,
                    tint = Theme.color.primary.primary
                )
            }
        }

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