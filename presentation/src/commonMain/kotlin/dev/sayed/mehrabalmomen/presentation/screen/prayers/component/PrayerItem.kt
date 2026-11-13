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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.am
import dev.sayed.mehrabalmomen.designsystem.resources.ic_next_prayer_arrow
import dev.sayed.mehrabalmomen.designsystem.resources.ic_volume_off
import dev.sayed.mehrabalmomen.designsystem.resources.ic_volume_on
import dev.sayed.mehrabalmomen.designsystem.resources.pm
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.UiText
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import org.jetbrains.compose.resources.painterResource

@Composable
fun PrayerItem(
    prayerName: Prayer.PrayerName,
    prayerLabel: UiText,
    prayerTime: String,
    isAm: Boolean,
    isNextPrayer: Boolean,
    isNotificationEnabled: Boolean = false,
    onNotificationClick: (Prayer.PrayerName, Boolean) -> Unit,
) {
    val language = LocalAppLocale.current
    val isAmStr = if (isAm) localizedString(Res.string.am) else localizedString(Res.string.pm)
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
                    painter = painterResource(Res.drawable.ic_next_prayer_arrow),
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
                text = prayerLabel.asString(),
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
            prayerTime.toLocalizedDigits(language) + " ${isAmStr}",
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
                        Res.drawable.ic_volume_on
                    else
                        Res.drawable.ic_volume_off
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
                            prayerName,
                            !isNotificationEnabled
                        )
                    }
            )
        }
    }
}
