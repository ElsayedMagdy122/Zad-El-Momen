package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.components

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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.DownloadState

@Composable
fun DownloadButton(
    downloadState: DownloadState,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
    downloadProgress: Int = 0,
    iconTint: Color = Theme.color.primary.primary
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .background(Color.Transparent)
            .clickable(
                enabled = downloadState == DownloadState.NOT_DOWNLOADED,
                onClick = onDownloadClick,
                interactionSource = MutableInteractionSource()
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = downloadState,
            transitionSpec = {
                (fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.8f)).togetherWith(
                    fadeOut(animationSpec = tween(110)) + scaleOut(targetScale = 0.8f)
                )
            },
            label = "DownloadButtonAnimation"
        ) { state ->
            when (state) {
                DownloadState.NOT_DOWNLOADED -> {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_download_01),
                        contentDescription = localizedString(R.string.downloaded),
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                DownloadState.DOWNLOADING -> {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { downloadProgress / 100f },
                            modifier = Modifier.size(28.dp),
                            color = Theme.color.semantic.success,
                            strokeWidth = 2.dp,
                            trackColor = Theme.color.semantic.success.copy(alpha = 0.3f),
                        )
                        Text(
                            text = "$downloadProgress",
                            style = Theme.textStyle.label.small,
                            color = Theme.color.semantic.success
                        )
                    }
                }

                DownloadState.DOWNLOADED -> {
                    // Left empty as the parent layout hides the DownloadButton when downloaded
                }

                DownloadState.FAILED -> {}
            }
        }
    }
}