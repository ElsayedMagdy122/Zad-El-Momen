package dev.sayed.mehrabalmomen.presentation.screen.reminders.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizeAmPm
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.screen.reminders.getReminderIcon
import dev.sayed.mehrabalmomen.presentation.screen.reminders.getReminderTitle
import java.util.Locale

@Composable
fun ReminderItem(
    config: ReminderConfig,
    onToggle: (Boolean) -> Unit,
    onTimeClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val language = LocalAppLocale.current

    val hour12 = when {
        config.hour == 0 -> 12
        config.hour > 12 -> config.hour - 12
        else -> config.hour
    }

    val amPm = if (config.hour >= 12) "PM" else "AM"

    val formattedTime = String.format(
        Locale.ENGLISH,
        "%02d:%02d %s",
        hour12,
        config.minute,
        amPm
    ).toLocalizedDigits(language)
        .localizeAmPm(language)
    val iconRes = getReminderIcon(config)
    Column(modifier = modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Theme.color.surfaces.surfaceLow)
                .clickable { onTimeClick() }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Theme.color.surfaces.surfaceHigh)
                    .padding(8.dp)
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = Theme.color.primary.primary
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = localizedString(getReminderTitle(config.type)),
                    color = Theme.color.primary.shadePrimary,
                    style = Theme.textStyle.label.medium
                )

                Text(
                    text = formattedTime,
                    color = Theme.color.secondary.shadeSecondary,
                    style = Theme.textStyle.label.small
                )
            }

            dev.sayed.mehrabalmomen.design_system.component.Switch(
                isChecked = config.isEnabled,
                onCheckedChange = onToggle
            )
        }
    }
}