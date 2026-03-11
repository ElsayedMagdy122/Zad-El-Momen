package dev.sayed.mehrabalmomen.presentation.screen.batteryOptimization.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString

@Composable
fun HeaderBox(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Theme.color.surfaces.surfaceLow)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.padding(end = 8.dp),
            painter = painterResource(R.drawable.ic_setting_error_03),
            contentDescription = null,
            tint = Theme.color.primary.primary
        )
        Text(
            text = localizedString(
                R.string.please_follow_the_instructions_below_to_ensure_that_adhan_notifications_are_received
            ),
            color = Theme.color.primary.primary,
            style = Theme.textStyle.title.small,
            textAlign = TextAlign.Start
        )
    }
}