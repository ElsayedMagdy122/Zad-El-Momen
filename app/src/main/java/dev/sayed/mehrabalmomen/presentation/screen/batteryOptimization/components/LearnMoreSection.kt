package dev.sayed.mehrabalmomen.presentation.screen.batteryOptimization.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.batteryOptimization.BatteryOptimizationInteractionListener

@Composable
fun LearnMoreSection(
    listener: BatteryOptimizationInteractionListener,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable(
                    interactionSource = MutableInteractionSource(),
                    indication = null
                ) {
                    listener.onLearnMoreClick()
                },
            painter = painterResource(R.drawable.ic_learn_more),
            contentDescription = null,
            tint = Theme.color.primary.primary
        )
        Text(
            modifier = Modifier.clickable(
                interactionSource = MutableInteractionSource(),
                indication = null
            ) {
                listener.onLearnMoreClick()
            },
            text = localizedString(R.string.more),
            color = Theme.color.primary.primary,
            style = Theme.textStyle.label.medium,
            textAlign = TextAlign.Center
        )
    }
}