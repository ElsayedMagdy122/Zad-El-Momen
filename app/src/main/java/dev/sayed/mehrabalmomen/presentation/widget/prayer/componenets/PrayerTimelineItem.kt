package dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetPrayer

@Composable
internal fun PrayerTimelineItem(
    prayer: PrayerWidgetPrayer,
    modifier: GlanceModifier,
) {
    val itemColor = if (prayer.isUpcoming) WidgetWhite else WidgetText

    Column(
        modifier = modifier
            .cornerRadius(12.dp)
            .background(if (prayer.isUpcoming) WidgetGold else WidgetWhite)
            .padding(horizontal = 2.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = prayer.name.uppercase(),
            style = TextStyle(
                color = itemColor,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
        )
        Spacer(modifier = GlanceModifier.height(3.dp))
        Image(
            provider = ImageProvider(prayer.iconRes),
            contentDescription = null,
            modifier = GlanceModifier.size(19.dp),
            colorFilter = ColorFilter.tint(if (prayer.isUpcoming) WidgetWhite else WidgetGold),
        )
        Spacer(modifier = GlanceModifier.height(3.dp))
        Text(
            text = prayer.time,
            style = TextStyle(
                color = itemColor,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
        )
    }
}
