package dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.batteryOptimization.BatteryOptimizationInteractionListener

@Composable
fun BatteryOptimizationHeader(
    listener: BatteryOptimizationInteractionListener,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AppBar(
            title = "",
            onBackClick = listener::onBackClicked
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.size(120.dp),
                painter = painterResource(id = R.drawable.bg_security),
                contentDescription = null,
            )
        }

        Column(
            modifier = Modifier.padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = localizedString(R.string.battery_optimization),
                color = Theme.color.primary.shadePrimary,
                style = Theme.textStyle.title.small
            )
            Text(
                modifier = Modifier.padding(top = 4.dp),
                text = localizedString(R.string.please_follow_the_instructions_below_to_ensure_that_adhan_notifications_are_received),
                color = Theme.color.secondary.shadeSecondary,
                style = Theme.textStyle.body.small,
                textAlign = TextAlign.Center
            )
        }
    }
}