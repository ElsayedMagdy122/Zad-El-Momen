package dev.sayed.mehrabalmomen.presentation.screen.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeUiState
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
fun UpComingPrayer(
    state: HomeUiState,
    countdownTime: HomeUiState.TimeUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow),
    ) {
        PrayerInfoSection(state)
        CountdownDisplay(countdownTime)
    }
}

@Composable
private fun PrayerInfoSection(state: HomeUiState) {
    key(state.nextPrayer) {
        Image(
            painter = painterResource(id = R.drawable.image_mosque_dark),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
            text = if (state.nextPrayer.name != 0)
                localizedString(R.string.time_until, localizedString(state.nextPrayer.name))
            else
                localizedString(R.string.no_remaining_time),
            color = Theme.color.primary.primary,
            style = Theme.textStyle.title.medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CountdownDisplay(
    countdownTime: HomeUiState.TimeUiState,
    modifier: Modifier = Modifier
) {
    CounterCard(
        timeUiState = countdownTime,
        modifier = modifier
            .padding(horizontal = 8.dp)
            .padding(top = 4.dp, bottom = 8.dp)
    )
}