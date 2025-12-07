package dev.sayed.mehrabalmomen.presentation.screen.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.CounterCard
import dev.sayed.mehrabalmomen.design_system.theme.Theme

@Composable
fun CounterCard(
    hours: String,
    minutes: String,
    seconds: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CounterCard(modifier = Modifier.weight(1f), text = hours)
            CounterCard(modifier = Modifier.weight(1f), text = minutes)
            CounterCard(modifier = Modifier.weight(1f), text = seconds)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                textAlign = TextAlign.Center,
                text = "Hours",
                color = Theme.color.secondary.secondaryText,
                style = Theme.textStyle.label.medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                textAlign = TextAlign.Center,
                text = "Minutes",
                color = Theme.color.secondary.secondaryText,
                style = Theme.textStyle.label.medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                textAlign = TextAlign.Center,
                text = "Seconds",
                color = Theme.color.secondary.secondaryText,
                style = Theme.textStyle.label.medium,
                modifier = Modifier.weight(1f)
            )
        }
    }

}