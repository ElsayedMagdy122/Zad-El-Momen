package dev.sayed.mehrabalmomen.presentation.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeUiState

@Composable
fun PrayersRowSection(
    prayers: List<HomeUiState.PrayerUiState>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ViewAllTodayPrayers(
            modifier = Modifier
                .padding(top = 12.dp, bottom = 8.dp)
                .padding(horizontal = 16.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(prayers) {
                PrayerItem(
                    prayerName = it.name,
                    prayerTime = it.time,
                    icon = it.icon,
                    isUpComing = it.isUpComing
                )
            }
        }
    }
}

@Composable
fun ViewAllTodayPrayers(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "Today's Prayers",
            color = Theme.color.primary.primary,
            style = Theme.textStyle.title.medium
        )
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(Theme.color.primary.primary)
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            Text(
                text = "View All",
                color = Theme.color.surfaces.surfaceHigh,
                style = Theme.textStyle.label.medium
            )
        }
    }
}

@Composable
private fun PrayerItem(
    prayerName: Int,
    prayerTime: String,
    icon: Int,
    isUpComing: Boolean = false,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isUpComing) {
        Theme.color.primary.primary
    } else {
        Theme.color.surfaces.surfaceLow
    }
    val iconColor = if (isUpComing) {
        Theme.color.surfaces.surfaceLow
    } else {
        Theme.color.secondary.secondaryText
    }
    val textColor = if (isUpComing) {
        Theme.color.surfaces.surfaceLow
    } else {
        Theme.color.primary.primary

    }
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (isUpComing) {
                    Modifier.border(
                        width = 5.dp,
                        color = Theme.color.primary.primary.copy(0.10f),
                        shape = RoundedCornerShape(16.dp)
                    )
                } else Modifier
            )
            .background(backgroundColor)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = iconColor
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = "${stringResource(prayerName)} ${prayerTime}",
            color = textColor,
            style = Theme.textStyle.label.medium
        )
    }
}