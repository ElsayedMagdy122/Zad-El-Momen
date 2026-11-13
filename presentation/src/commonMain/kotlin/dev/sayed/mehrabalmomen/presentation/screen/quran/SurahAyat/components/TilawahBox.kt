package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.ic_backward_02
import dev.sayed.mehrabalmomen.designsystem.resources.ic_close
import dev.sayed.mehrabalmomen.designsystem.resources.ic_curvy_right_direction
import dev.sayed.mehrabalmomen.designsystem.resources.ic_forward_02
import dev.sayed.mehrabalmomen.designsystem.resources.ic_pause
import dev.sayed.mehrabalmomen.designsystem.resources.ic_play
import dev.sayed.mehrabalmomen.designsystem.resources.ic_repeat_one_01
import dev.sayed.mehrabalmomen.designsystem.resources.ic_user_list
import dev.sayed.mehrabalmomen.designsystem.resources.select_reciter
import dev.sayed.mehrabalmomen.designsystem.resources.surah_ayah_format
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.utils.toLocalizedDigits
import org.jetbrains.compose.resources.painterResource

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
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {
        Header(
            readerName = readerName,
            onCloseClick = onCloseClick,
            onRecitersClick = onRecitersClick,
            modifier = Modifier.padding(bottom = 2.dp)
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
private fun Header(
    readerName: String,
    onCloseClick: () -> Unit,
    onRecitersClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val displayName = readerName.ifBlank { localizedString(Res.string.select_reciter) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Theme.color.surfaces.surfaceLow)
            .clickable(onClick = onRecitersClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = displayName,
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            painter = painterResource(Res.drawable.ic_close),
            contentDescription = null,
            tint = Theme.color.primary.primary,
            modifier = Modifier.size(14.dp).clickable(
                onClick = onCloseClick,
                interactionSource = MutableInteractionSource()
            )
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
        Res.string.surah_ayah_format,
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
                    radius = 4.dp,
                    offset = DpOffset(0.dp, (-2).dp),
                    color = Color.Black.copy(alpha = 0.06f)
                )
            )
            .background(Theme.color.surfaces.surfaceLow)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formattedAyahText,
            style = Theme.textStyle.label.medium,
            color = Theme.color.secondary.shadeSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

        Row(
            modifier = Modifier
                .padding(top = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_user_list),
                contentDescription = null,
                tint = Theme.color.primary.primary,
                modifier = Modifier.clickable { onRecitersClick() }
            )

            Row(
                modifier = Modifier
                    .widthIn(min = 192.dp)
                    .graphicsLayer {
                        scaleX = if (isRtl) 1f else -1f
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_repeat_one_01),
                    contentDescription = null,
                    tint = if (repeatCount > 0) Theme.color.primary.primary else Theme.color.semantic.shadeTertiary,
                    modifier = Modifier.clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = onRepeatClick
                    )
                )

                Icon(
                    painter = painterResource(Res.drawable.ic_curvy_right_direction),
                    contentDescription = null,
                    tint = if (isContinuous) Theme.color.primary.primary else Theme.color.semantic.shadeTertiary,
                    modifier = Modifier.clickable(
                        interactionSource = MutableInteractionSource(),
                        indication = null,
                        onClick = onContinuousClick
                    )
                )

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Theme.color.surfaces.surface)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = onBackwardClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_backward_02),
                        contentDescription = null,
                        tint = Theme.color.primary.primary
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Theme.color.surfaces.surface)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = onPlayPauseClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = isLoading to isPlaying,
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.8f)).togetherWith(
                                fadeOut(animationSpec = tween(110)) + scaleOut(targetScale = 0.8f)
                            )
                        },
                        label = "PlayPauseAnimation"
                    ) { (loading, playing) ->
                        if (loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Theme.color.primary.primary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            if (playing) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_pause),
                                    contentDescription = null,
                                    tint = Theme.color.primary.primary
                                )
                            } else {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_play),
                                    contentDescription = null,
                                    tint = Theme.color.primary.primary
                                )
                            }
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Theme.color.surfaces.surface)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null,
                            onClick = onForwardClick
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_forward_02),
                        contentDescription = null,
                        tint = Theme.color.primary.primary
                    )
                }
            }
        }
    }
}
