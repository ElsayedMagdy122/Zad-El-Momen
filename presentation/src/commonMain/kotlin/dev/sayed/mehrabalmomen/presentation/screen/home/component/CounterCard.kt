package dev.sayed.mehrabalmomen.presentation.screen.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeUiState
import dev.sayed.mehrabalmomen.presentation.utils.toLocalizedDigits

@Composable
fun CounterCard(
    timeUiState: HomeUiState.TimeUiState,
    modifier: Modifier = Modifier
) {
    val language = LocalAppLocale.current

    Column(
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            CounterCard(
                modifier = Modifier.weight(1f),
                text = timeUiState.hours.toLocalizedDigits(language)
            )
            CounterCard(
                modifier = Modifier.weight(1f),
                text = timeUiState.minutes.toLocalizedDigits(language)
            )
            CounterCard(
                modifier = Modifier.weight(1f),
                text = timeUiState.seconds.toLocalizedDigits(language)
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = localizedString(Res.string.hours),
                color = Theme.color.primary.primary,
                style = Theme.textStyle.label.medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                textAlign = TextAlign.Center,
                text = localizedString(Res.string.minutes),
                color = Theme.color.primary.primary,
                style = Theme.textStyle.label.medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                textAlign = TextAlign.Center,
                text = localizedString(Res.string.seconds),
                color = Theme.color.primary.primary,
                style = Theme.textStyle.label.medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CounterCard(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Theme.color.primary.primary,
            style = Theme.textStyle.title.medium
        )
    }
}
