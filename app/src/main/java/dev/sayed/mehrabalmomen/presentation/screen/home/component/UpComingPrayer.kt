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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeUiState

@Composable
fun UpComingPrayer(
    state: HomeUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow),
    ) {
        Image(
            painter = painterResource(id = R.drawable.night_mosque),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
            text = localizedString(R.string.upcoming_prayer),
            color = Theme.color.secondary.secondaryText,
            style = Theme.textStyle.label.medium,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp),
            text =
                if (state.nextPrayer.name != 0)
                    "${context.getString(state.nextPrayer.name)} – ${state.nextPrayer.time}"
                else
                    localizedString(R.string.no_upcoming_prayer),
            color = Theme.color.primary.primary,
            style = Theme.textStyle.title.large,
            textAlign = TextAlign.Center
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            text =
                if (state.nextPrayer.name != 0)
                    stringResource(R.string.time_until, context.getString(state.nextPrayer.name))
                else
                    localizedString(R.string.no_remaining_time),
            color = Theme.color.secondary.secondaryText,
            style = Theme.textStyle.label.medium,
            textAlign = TextAlign.Center
        )
        CounterCard(
            timeUiState = state.time,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(top = 4.dp, bottom = 8.dp),
        )
    }
}