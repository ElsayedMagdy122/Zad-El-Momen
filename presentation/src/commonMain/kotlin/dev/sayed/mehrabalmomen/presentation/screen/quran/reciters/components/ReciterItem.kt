package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.DownloadState
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.ReciterUiState
import org.jetbrains.compose.resources.painterResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.downloaded
import zadelmomen.presentation.generated.resources.ic_tick_double_02

@Composable
fun ReciterItem(
    state: ReciterUiState,
    onPlayClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier,
    isArabic: Boolean = true
) {
    val reciterName = if (isArabic) state.nameAr else state.nameEn
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .then(
                if (state.isSelected) {
                    Modifier.border(
                        width = 1.dp,
                        color = Theme.color.primary.primary,
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            )
            .background(Theme.color.surfaces.surfaceLow)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayButton(
            playState = state.playState,
            onPlayClick = onPlayClick,
            modifier = Modifier.size(40.dp)
        )
        ReciterInformation(
            name = reciterName,
            isDownloaded = state.downloadState == DownloadState.DOWNLOADED,
            rewayaName = state.rewayaName,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
        )
        if (state.downloadState != DownloadState.DOWNLOADED) {
            DownloadButton(
                downloadState = state.downloadState,
                downloadProgress = state.downloadProgress,
                onDownloadClick = onDownloadClick
            )
        }
    }
}

@Composable
fun ReciterInformation(
    name: String,
    rewayaName: String,
    isDownloaded: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = name,
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary
        )
        Row {
            Text(
                text = rewayaName,
                style = Theme.textStyle.label.small,
                color = Theme.color.secondary.shadeSecondary
            )
            if (isDownloaded) {
                Icon(
                    painter = painterResource(Res.drawable.ic_tick_double_02),
                    contentDescription = null,
                    tint = Theme.color.semantic.success,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(12.dp)
                )
                Text(
                    text = localizedString(Res.string.downloaded),
                    style = Theme.textStyle.label.small,
                    color = Theme.color.semantic.success
                )
            }
        }
    }
}
