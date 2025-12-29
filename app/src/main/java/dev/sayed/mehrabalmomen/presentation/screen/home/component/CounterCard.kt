package dev.sayed.mehrabalmomen.presentation.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeUiState

@Composable
fun CounterCard(
    timeUiState: HomeUiState.TimeUiState,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CounterCard(modifier = Modifier.weight(1f), text = timeUiState.hours)
            CounterCard(modifier = Modifier.weight(1f), text = timeUiState.minutes)
            CounterCard(modifier = Modifier.weight(1f), text = timeUiState.seconds)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = localizedString(R.string.hours),
                color = Theme.color.secondary.secondaryText,
                style = Theme.textStyle.label.medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                textAlign = TextAlign.Center,
                text = localizedString(R.string.minutes),
                color = Theme.color.secondary.secondaryText,
                style = Theme.textStyle.label.medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                textAlign = TextAlign.Center,
                text = localizedString(R.string.seconds),
                color = Theme.color.secondary.secondaryText,
                style = Theme.textStyle.label.medium,
                modifier = Modifier.weight(1f)
            )
        }
    }

}

@Composable
private fun CounterCard(
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceHigh)
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = Theme.color.primary.primary,
            style = Theme.textStyle.title.large
        )
    }
}