@file:OptIn(ExperimentalTime::class)

package dev.sayed.mehrabalmomen.presentation.screen.prayers.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.screen.prayers.FullPrayerTimesUiState
import kotlin.time.ExperimentalTime

@Composable
fun TimerCounter(time : FullPrayerTimesUiState.TimeUiState, modifier: Modifier = Modifier) {
    val language = LocalAppLocale.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.primary.primary)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = time.hours.toLocalizedDigits(language),
            color = Theme.color.surfaces.surfaceHigh,
            style = Theme.textStyle.title.extraLarge
        )
        Dots(modifier = Modifier.padding(horizontal = 8.dp))
        Text(
            text =  time.minutes.toLocalizedDigits(language),
            color = Theme.color.surfaces.surfaceHigh,
            style = Theme.textStyle.title.extraLarge
        )
        Dots(modifier = Modifier.padding(horizontal = 8.dp))
        Text(
            text = time.seconds.toLocalizedDigits(language),
            color = Theme.color.surfaces.surfaceHigh,
            style = Theme.textStyle.title.extraLarge
        )
    }
}
@Composable
private fun Dots(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(Theme.color.secondary.secondaryText)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(Theme.color.secondary.secondaryText)
        )
    }
}