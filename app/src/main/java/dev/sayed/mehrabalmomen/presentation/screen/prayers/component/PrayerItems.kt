package dev.sayed.mehrabalmomen.presentation.screen.prayers.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.components.LinearProgress
import dev.sayed.mehrabalmomen.presentation.screen.prayers.FullPrayerTimesUiState
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Composable
fun PrayerItems(
    prayers: List<FullPrayerTimesUiState.PrayerUiState>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow)
    ) {
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            text = stringResource(R.string.today_s_full_timetable),
            color = Theme.color.primary.primary,
            style = Theme.textStyle.title.large
        )
        LazyVerticalGrid(
            modifier = Modifier
                .padding(top = 12.dp)
                .heightIn(max = 10_000.dp),
            columns = GridCells.Adaptive(minSize = 320.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(
                bottom = 8.dp,
                start = 16.dp,
                end = 16.dp
            )
        ) {
            items(prayers) {
                PrayerItem(
                    isUpComing = it.isUpComing,
                    prayer = it
                )
            }
        }
    }
}

@ExperimentalTime
@Composable
fun PrayerItem(
    isUpComing: Boolean = false,
    prayer: FullPrayerTimesUiState.PrayerUiState,
    modifier: Modifier = Modifier
) {
    val backgroundItem =
        if (isUpComing) Theme.color.primary.primary else Theme.color.surfaces.surfaceHigh
    val prayerNameColor =
        if (isUpComing) Theme.color.secondary.secondaryText else Theme.color.primary.primary
    Row(
        modifier
            .width(320.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundItem)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(prayer.icon),
            contentDescription = null,
            tint = Theme.color.secondary.secondaryText,
            modifier = Modifier
                .padding(6.dp)
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(prayer.name),
                color = prayerNameColor,
                style = Theme.textStyle.title.small
            )
            LinearProgress(
                progress = prayer.progress,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth()
            )
        }

        Text(
            modifier = Modifier.padding(end = 8.dp),
            text = prayer.time,
            color = Theme.color.secondary.secondaryText,
            style = Theme.textStyle.title.small
        )
    }
}


@Preview
@Composable
private fun PrayerItemPreview() {
    MehrabTheme(isDarkTheme = false) {
        // PrayerItem()
    }
}