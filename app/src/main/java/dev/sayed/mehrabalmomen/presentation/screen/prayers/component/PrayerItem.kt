package dev.sayed.mehrabalmomen.presentation.screen.prayers.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits

@Composable
fun PrayerItem(
    prayerNameResource: Int,
    prayerTime: String,
    isAm: Boolean,
    isNextPrayer: Boolean,
    isNotificationEnabled: Boolean = false,
    onNotificationClick: (Prayer.PrayerName, Boolean) -> Unit,
) {
    val language = LocalAppLocale.current
    val isAm = if (isAm) localizedString(R.string.am) else localizedString(R.string.pm)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(bottom = 8.dp)
            .padding(horizontal = 16.dp)
            .background(
                Theme.color.surfaces.surfaceLow,
                shape = RoundedCornerShape(12.dp)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isNextPrayer) {
                Icon(
                    painter = painterResource(R.drawable.ic_next_prayer_arrow),
                    tint = Theme.color.secondary.secondaryText,
                    contentDescription = "",
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .graphicsLayer {
                            scaleX = if (isRtl) -1f else 1f
                        }
                )
            }

            Text(
                text = localizedString(prayerNameResource),
                style = Theme.textStyle.label.medium,
                color = Theme.color.secondary.shadeSecondary,
                modifier = Modifier.padding(
                    start = if (isNextPrayer) 0.dp else 16.dp,
                    top = 12.dp,
                    bottom = 12.dp
                )
            )
        }

        Text(
            prayerTime.toLocalizedDigits(language) + " ${isAm}",
            //style = Theme.textStyle.label.large,
            style = Theme.textStyle.label.medium,
            color = Theme.color.primary.shadePrimary,
            modifier = Modifier.padding(
                end = 12.dp,
                top = 12.dp,
                bottom = 12.dp
            )
        )
        AnimatedContent(
            targetState = isNotificationEnabled,
            transitionSpec = {
                fadeIn(animationSpec = tween(200)) togetherWith
                        fadeOut(animationSpec = tween(200))
            },
            label = "NotificationIconAnimation"
        ) { enabled ->
            Icon(
                painter = painterResource(
                    if (isNotificationEnabled)
                        R.drawable.ic_volume_on
                    else
                        R.drawable.ic_volume_off
                ),
                contentDescription = null,
                tint = Theme.color.secondary.secondaryText,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Theme.color.surfaces.surfaceHigh)
                    .padding(6.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onNotificationClick(
                            prayerNameResource.toPrayerName(),
                            !isNotificationEnabled
                        )
                    }
            )
        }
    }
}

fun Int.toPrayerName(): Prayer.PrayerName {
    return when (this) {
        R.string.fajr -> Prayer.PrayerName.FAJR
        R.string.dhuhr -> Prayer.PrayerName.ZUHR
        R.string.asr -> Prayer.PrayerName.ASR
        R.string.maghrib -> Prayer.PrayerName.MAGHRIB
        R.string.isha -> Prayer.PrayerName.ISHA
        else -> Prayer.PrayerName.FAJR
    }

}

@Preview
@Composable
private fun Preview() {
    MehrabTheme {

        PrayerItem(
            prayerNameResource = R.string.fajr,
            prayerTime = "1:20 AM",
            isNextPrayer = true,
            isAm = true,
            onNotificationClick = { _, _ -> }
        )

    }
}

@Preview
@Composable
private fun PrayerItemPreview() {
    MehrabTheme(isDarkTheme = false) {
        // PrayerItem()
    }
}