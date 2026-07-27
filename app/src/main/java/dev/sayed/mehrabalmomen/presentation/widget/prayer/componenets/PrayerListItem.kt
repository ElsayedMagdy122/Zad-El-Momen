package dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetPrayer

@Composable
internal fun PrayerListItem(prayer: PrayerWidgetPrayer) {
    val itemColor = if (prayer.isUpcoming) WidgetGold else WidgetText

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .height(27.dp)
            .padding(horizontal = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            provider = ImageProvider(prayer.iconRes),
            contentDescription = null,
            modifier = GlanceModifier.size(16.dp),
            colorFilter = ColorFilter.tint(WidgetGold),
        )
        Spacer(modifier = GlanceModifier.width(7.dp))
        Text(
            text = prayer.name.uppercase(),
            style = TextStyle(
                color = itemColor,
                fontSize = 9.sp,
                fontWeight = if (prayer.isUpcoming) FontWeight.Bold else FontWeight.Medium,
            ),
            maxLines = 1,
        )
        Spacer(modifier = GlanceModifier.defaultWeight())
        Text(
            text = prayer.time,
            style = TextStyle(
                color = itemColor,
                fontSize = 9.sp,
                fontWeight = if (prayer.isUpcoming) FontWeight.Bold else FontWeight.Medium,
            ),
            maxLines = 1,
        )
    }
}
