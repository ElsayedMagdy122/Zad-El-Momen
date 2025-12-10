package dev.sayed.mehrabalmomen.presentation.screen.calibrate_device.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.components.CardIcon
import dev.sayed.mehrabalmomen.presentation.screen.calibrate_device.Steps


fun LazyGridScope.stepsCard(
    steps: List<Steps>
) {
    items(steps) {
        StepsItem(
            icon = painterResource(id = it.icon),
            title = it.title,
            description = it.description
        )
    }
}

@Composable
private fun StepsItem(
    icon: Painter,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CardIcon(
            icon = icon,
            contentDescription = null
        )
        Column(
            modifier = Modifier.padding(start = 16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                color = Theme.color.primary.primary,
                style = Theme.textStyle.title.small
            )
            Text(
                text = description,
                color = Theme.color.secondary.shadeSecondary,
                style = Theme.textStyle.body.small
            )
        }
    }
}