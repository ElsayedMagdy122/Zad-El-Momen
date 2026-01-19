package dev.sayed.mehrabalmomen.presentation.screen.quran.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahUiState

@Composable
fun SurahItem(
    surahUiState: SurahUiState,
    onClick: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val surahName = if (isRtl) surahUiState.nameArabic else surahUiState.nameEnglish
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .clickable { onClick(surahUiState.id, surahName) }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AyahNumber(number = surahUiState.id)
        SurahInfo(surahUiState, surahName)
        SurahArabicName(surahUiState.surahImage)
    }
}

@Composable
private fun RowScope.SurahInfo(
    surahUiState: SurahUiState,
    surahName: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .padding(start = 12.dp)
            .weight(1f)
    ) {
        Text(
            text = surahName,
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary
        )
        Row(
            modifier = Modifier
                .padding(top = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_quran),
                tint = Theme.color.secondary.shadeSecondary,
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(start = 4.dp, end = 8.dp),
                text = localizedString(R.string.ayat, surahUiState.ayahNumbers),
                style = Theme.textStyle.label.small,
                color = Theme.color.secondary.shadeSecondary
            )
            Box(
                modifier = Modifier
                    .size(3.dp)
                    .clip(CircleShape)
                    .background(Theme.color.secondary.shadeSecondary)
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = surahUiState.surahType,
                style = Theme.textStyle.label.small,
                color = Theme.color.secondary.shadeSecondary
            )
        }
    }
}

@Composable
private fun SurahArabicName(image: Int) {
    Icon(
        painter = painterResource(image),
        modifier = Modifier.size(48.dp),
        contentDescription = null,
        tint = Theme.color.primary.shadePrimary
    )
}

@SuppressLint("SuspiciousIndentation")
@Composable
private fun AyahNumber(number: Int, modifier: Modifier = Modifier) {
    val surahNumber = if (number <= 9) "0${number}" else number.toString()
    val language = LocalAppLocale.current
    Box(
        modifier = modifier.size(36.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_sura_number),
            contentDescription = null,
            tint = Theme.color.secondary.secondary,
        )
        Text(
            text =surahNumber.toLocalizedDigits(language),
            style = Theme.textStyle.label.small,
            color = Theme.color.secondary.secondary
        )
    }
}