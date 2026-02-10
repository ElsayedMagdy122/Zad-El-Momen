package dev.sayed.mehrabalmomen.presentation.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeUiState

@Composable
fun ContinueToTilawah(
    onClick: () -> Unit,
    surahUiState: HomeUiState.ContinueTilawahUi,
    modifier: Modifier = Modifier
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val surahName = if (isRtl) surahUiState.nameArabic else surahUiState.nameEnglish

    Column(modifier = modifier) {
        TilawahTitle()
        TilawahRow(
            surahName = surahName,
            ayahId = surahUiState.ayahId,
            onClick = onClick,
            isRtl = isRtl
        )
    }
}

@Composable
private fun TilawahTitle() {
    Text(
        text = localizedString(R.string.tilawah),
        color = Theme.color.primary.shadePrimary,
        style = Theme.textStyle.title.small
    )
}

@Composable
private fun TilawahRow(
    surahName: String,
    ayahId: Int,
    onClick: () -> Unit,
    isRtl: Boolean
) {
    Row(
        modifier = Modifier
            .padding(top = 8.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TilawahIconBox()
        Column(
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f)
        ) {
            Text(
                text = surahName,
                color = Theme.color.primary.shadePrimary,
                style = Theme.textStyle.label.medium
            )
            Text(
                text = ayahId.toString(),
                color = Theme.color.secondary.shadeSecondary,
                style = Theme.textStyle.label.small
            )
        }
        TilawahContinueSection(isRtl = isRtl)
    }
}

@Composable
private fun TilawahIconBox() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.color.surfaces.surfaceHigh)
            .padding(8.dp)
    ) {
        Icon(
            modifier = Modifier.size(24.dp),
            painter = painterResource(id = R.drawable.ic_quran02),
            contentDescription = null,
            tint = Theme.color.primary.primary
        )
    }
}

@Composable
private fun TilawahContinueSection(isRtl: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = localizedString(R.string.continue_tilawah),
            color = Theme.color.primary.shadePrimary,
            style = Theme.textStyle.label.medium
        )
        Icon(
            modifier = Modifier
                .padding(start = 4.dp)
                .size(16.dp)
                .graphicsLayer {
                    scaleX = if (isRtl) 1f else -1f
                },
            painter = painterResource(id = R.drawable.ic_arrow_left_01),
            contentDescription = null,
            tint = Theme.color.primary.primary
        )
    }
}