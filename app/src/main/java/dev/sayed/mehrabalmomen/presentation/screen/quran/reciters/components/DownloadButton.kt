package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.DownloadState

@Composable
fun DownloadButton(
    downloadState: DownloadState,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    progressTint: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier
            .clickable(
                enabled = downloadState == DownloadState.NOT_DOWNLOADED,
                onClick = onDownloadClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = downloadState,
            animationSpec = tween(durationMillis = 200),
            label = "DownloadButtonCrossfade"
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
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = progressTint,
                        strokeWidth = 2.5.dp
                    )
                }

                DownloadState.DOWNLOADED -> {
                    // Left empty as the parent layout hides the DownloadButton when downloaded
                }
            }
        }
    }
}