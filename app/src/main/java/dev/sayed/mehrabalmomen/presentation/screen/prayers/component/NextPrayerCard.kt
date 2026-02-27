package dev.sayed.mehrabalmomen.presentation.screen.prayers.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.screen.prayers.FullPrayerTimesUiState
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun NextPrayerCard(
    state: FullPrayerTimesUiState,
    countdownTime: FullPrayerTimesUiState.TimeUiState,
    modifier: Modifier = Modifier
) {
    val language = LocalAppLocale.current
    Row(
        modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 24.dp, bottom = 16.dp)
            .background(
                Theme.color.surfaces.surfaceLow,
                shape = RoundedCornerShape(12.dp)
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.ic_prayer_man),
            contentDescription = "prayer man icon",
            modifier = Modifier
                .padding(start = 12.dp)
                .padding(vertical = 8.dp)
                .size(40.dp)
        )
        val nextPrayerText = if (state.nextPrayer.name != 0) {

            val prayerName = localizedString(state.nextPrayer.name)
            localizedString(R.string.next_prayer_in, prayerName)
        } else {
            localizedString(R.string.no_upcoming_prayer)
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = nextPrayerText,
                style = Theme.textStyle.label.medium,
                color = Theme.color.secondary.shadeSecondary
            )
            val time = "${countdownTime.hours.toLocalizedDigits(language)}:" +
                    "${countdownTime.minutes.toLocalizedDigits(language)}:" +
                    countdownTime.seconds.toLocalizedDigits(language)
            Text(
                text = time,
                style = Theme.textStyle.title.medium,
                color = Theme.color.secondary.secondary
            )
        }
    }
}