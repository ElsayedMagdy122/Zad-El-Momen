package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.DownloadState
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.PlayState
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.ReciterUiState

@Composable
fun ReciterItem(
    state: ReciterUiState,
    onPlayClick: () -> Unit,
    onDownloadClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(Theme.color.surfaces.surfaceLow)
            .padding(8.dp)
    ) {
        PlayButton(
            playState = state.playState,
            onPlayClick = onPlayClick,
            modifier = Modifier.size(40.dp)
        )
        ReciterInformation(
            name = state.name,
            isDownloaded = state.downloadState == DownloadState.DOWNLOADED,
            rewayaName = state.rewayaName,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
        )
        if (state.downloadState != DownloadState.DOWNLOADED) {
            DownloadButton(
                downloadState = state.downloadState,
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
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Column(modifier = modifier) {
        Text(
            text = name,
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary
        )
        Row() {
            Text(
                text = rewayaName,
                style = Theme.textStyle.label.small,
                color = Theme.color.secondary.shadeSecondary
            )
            if (isDownloaded) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_tick_double_02),
                    contentDescription = null,
                    tint = Theme.color.semantic.success,
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(12.dp)
                        .graphicsLayer {
                            if (isRtl) {
                                scaleX = -1f
                            }
                        }
                )
                Text(
                    text = localizedString(R.string.downloaded),
                    style = Theme.textStyle.label.small,
                    color = Theme.color.semantic.success
                )
            }
        }
    }
}

@Composable
private fun ReciterItemsPreviewContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ReciterItem(
            state = ReciterUiState(
                id = 1,
                name = "عبد الباسط عبد الصمد",
                rewayaName = "حفص عن عاصم - مجود",
                baseAudioUrl = "",
                downloadState = DownloadState.NOT_DOWNLOADED,
                playState = PlayState.RESUME
            ),
            onPlayClick = {},
            onDownloadClick = {}
        )

        ReciterItem(
            state = ReciterUiState(
                id = 2,
                name = "محمود خليل الحصري",
                rewayaName = "ورش عن نافع",
                baseAudioUrl = "",
                downloadState = DownloadState.DOWNLOADING,
                playState = PlayState.LOADING
            ),
            onPlayClick = {},
            onDownloadClick = {}
        )

        ReciterItem(
            state = ReciterUiState(
                id = 3,
                name = "محمد صديق المنشاوي",
                rewayaName = "حفص عن عاصم - معلم",
                baseAudioUrl = "",
                downloadState = DownloadState.DOWNLOADED,
                playState = PlayState.PLAY
            ),
            onPlayClick = {},
            onDownloadClick = {}
        )
    }
}

@Preview
@Composable
fun ReciterItemPreview() {
    MehrabTheme {
        ReciterItemsPreviewContent()
    }
}


