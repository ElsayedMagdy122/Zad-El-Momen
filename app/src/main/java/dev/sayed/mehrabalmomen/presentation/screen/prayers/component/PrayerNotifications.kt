@file:OptIn(ExperimentalTime::class)

package dev.sayed.mehrabalmomen.presentation.screen.prayers.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.entity.Prayer
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.prayers.FullPrayerTimeInteractionListener
import dev.sayed.mehrabalmomen.presentation.screen.prayers.FullPrayerTimesUiState
import kotlin.time.ExperimentalTime

@Composable
fun PrayerNotifications(
    prayerNotifications: List<FullPrayerTimesUiState.PrayerNotificationUiState>,
    listener: FullPrayerTimeInteractionListener,
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
            text = localizedString(R.string.prayer_notifications),
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
            items(prayerNotifications) { state->
                NotificationItem(
                    prayerNotificationUiState = state,
                    onCheckedChange = { isChecked ->
                        listener.onClickEnablePrayer(
                            prayerName = state.name.toPrayerName(),
                            isEnabled = isChecked
                        )
                    }
                )
            }
        }
    }
}

fun Int.toPrayerName(): Prayer.PrayerName{
    return when(this){
        R.string.fajr -> Prayer.PrayerName.FAJR
        R.string.dhuhr -> Prayer.PrayerName.ZUHR
        R.string.asr -> Prayer.PrayerName.ASR
        R.string.maghrib -> Prayer.PrayerName.MAGHRIB
        R.string.isha -> Prayer.PrayerName.ISHA
        else -> Prayer.PrayerName.FAJR
    }

}


@Composable
fun NotificationItem(
    prayerNotificationUiState: FullPrayerTimesUiState.PrayerNotificationUiState,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .width(320.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceHigh)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f),
            text = stringResource(prayerNotificationUiState.name),
            color = Theme.color.primary.primary,
            style = Theme.textStyle.title.small
        )
        dev.sayed.mehrabalmomen.design_system.component.Switch(
            isChecked = prayerNotificationUiState.isEnabled,
            onCheckedChange = { isChecked ->
                onCheckedChange(isChecked)
            }
        )
    }
}

@Preview
@Composable
private fun NotificationItemPreview() {
    NotificationItem(
        prayerNotificationUiState = FullPrayerTimesUiState.PrayerNotificationUiState(name = R.string.fajr),
        onCheckedChange = {}
    )
}