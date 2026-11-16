package dev.sayed.mehrabalmomen.presentation.screen.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.LocalIsDarkTheme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeUiState
import org.jetbrains.compose.resources.painterResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.ic_mosque_dark_bg
import zadelmomen.presentation.generated.resources.ic_mosque_light_bg
import zadelmomen.presentation.generated.resources.mosque_column
import zadelmomen.presentation.generated.resources.ic_triangle_down
import zadelmomen.presentation.generated.resources.am
import zadelmomen.presentation.generated.resources.pm

@Composable
fun PrayerTimesCard(
    prayerTimesUiState: HomeUiState?,
    modifier: Modifier = Modifier
) {
    if (prayerTimesUiState == null) return
    val isDark = LocalIsDarkTheme.current
    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(2.65f)
                .fillMaxSize()
                .align(Alignment.BottomCenter)
                .padding(top = 16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Theme.color.surfaces.surfaceLow)
        )
        val image = if (isDark) Res.drawable.ic_mosque_dark_bg else Res.drawable.ic_mosque_light_bg
        Image(
            painter = painterResource(image),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.TopEnd)
                .fillMaxWidth(0.35f)
                .aspectRatio(1.52f),
            contentScale = ContentScale.Fit,
        )
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.BottomCenter),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly,
            contentPadding = PaddingValues(horizontal = 12.dp),
        ) {
            itemsIndexed(prayerTimesUiState.prayers) { index, prayer ->
                PrayerItem(
                    prayer = prayer,
                    isNextPrayer = prayer.isUpComing
                )
            }
        }

        Image(
            painter = painterResource(Res.drawable.mosque_column),
            contentDescription = "",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .fillMaxHeight()
                .padding(start = 22.dp),
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun PrayerItem(
    prayer: HomeUiState.PrayerUiState,
    isNextPrayer: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        val language = LocalAppLocale.current
        Column(
            modifier = Modifier
                .padding(bottom = 12.dp)
                .padding(horizontal = 2.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = prayer.name.asString(),
                color = Theme.color.secondary.secondaryText,
                style = Theme.textStyle.label.small,
            )
            Text(
                text = prayer.time.toLocalizedDigits(language),
                color = Theme.color.secondary.secondary,
                style = Theme.textStyle.title.medium,
            )
            Text(
                text = if (prayer.isAm) localizedString(Res.string.am)
                else localizedString(Res.string.pm),
                style = Theme.textStyle.label.small,
                color = Theme.color.secondary.secondaryText,
            )
        }

        if (isNextPrayer) {
            Icon(
                painter = painterResource(Res.drawable.ic_triangle_down),
                contentDescription = null,
                tint = Theme.color.secondary.secondaryText,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .size(16.dp),
            )
        }
    }
}
