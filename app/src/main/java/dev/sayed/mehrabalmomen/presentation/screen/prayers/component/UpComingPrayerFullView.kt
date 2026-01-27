@file:OptIn(ExperimentalTime::class)

package dev.sayed.mehrabalmomen.presentation.screen.prayers.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizeAmPm
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.screen.prayers.FullPrayerTimesUiState
import kotlin.time.ExperimentalTime

@Composable
fun UpComingPrayerFullView(
    state: FullPrayerTimesUiState,
    countdownTime: FullPrayerTimesUiState.TimeUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val language = LocalAppLocale.current
    val localizedTime = state.nextPrayer.time
        .toLocalizedDigits(language)
        .localizeAmPm(language)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow),
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                    text = localizedString(R.string.upcoming_prayer),
                    color = Theme.color.secondary.secondary,
                    style = Theme.textStyle.label.medium,
                    textAlign = TextAlign.Center
                )

                val prayerName = if (state.nextPrayer.name != 0)
                    localizedString(state.nextPrayer.name)
                else
                    localizedString(R.string.no_upcoming_prayer)

                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 4.dp),
                    text = if (state.nextPrayer.name != 0)
                        "$prayerName – $localizedTime"
                    else
                        localizedString(R.string.no_upcoming_prayer),
                    color = Theme.color.primary.primary,
                    style = Theme.textStyle.title.large,
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(40.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Theme.color.surfaces.surfaceHigh),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = if (state.nextPrayer.icon != 0)
                        painterResource(state.nextPrayer.icon)
                    else
                        painterResource(id = R.drawable.mosque_02),
                    contentDescription = null,
                    tint = Theme.color.primary.primary,
                    modifier = Modifier
                        .padding(6.dp)

                )
            }
        }

        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            text =
                if (state.nextPrayer.name != 0)
                    localizedString(R.string.time_remaining)
                else
                    localizedString(R.string.no_remaining_time),
            color = Theme.color.secondary.secondaryText,
            style = Theme.textStyle.label.medium,
            textAlign = TextAlign.Center
        )
        TimerCounter(
            time = countdownTime,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(top = 4.dp, bottom = 8.dp),
        )
    }
}