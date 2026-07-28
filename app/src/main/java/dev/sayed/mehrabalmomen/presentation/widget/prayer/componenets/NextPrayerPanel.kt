package dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
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
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetUiState
import dev.sayed.mehrabalmomen.presentation.widget.prayer.localizedString

@Composable
internal fun NextPrayerPanel(
    state: PrayerWidgetUiState,
    modifier: GlanceModifier,
) {
    val context = LocalContext.current
    val nextPrayer = state.upcomingPrayer()

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = state.localizedString(context, R.string.prayer_widget_next).uppercase(),
            style = TextStyle(
                color = WidgetGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
        )
        if (state.isTomorrow) {
            Spacer(modifier = GlanceModifier.height(2.dp))
            Text(
                text = state.localizedString(context, R.string.prayer_widget_tomorrow),
                style = TextStyle(
                    color = WidgetGold,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                ),
                maxLines = 1,
            )
        }
        Spacer(modifier = GlanceModifier.height(5.dp))
        Image(
            provider = ImageProvider(R.drawable.mosque_02),
            contentDescription = null,
            modifier = GlanceModifier.size(42.dp),
            colorFilter = ColorFilter.tint(WidgetGold),
        )
        Spacer(modifier = GlanceModifier.height(5.dp))
        Text(
            text = state.nextPrayerName.uppercase(),
            style = TextStyle(
                color = WidgetWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
            modifier = GlanceModifier
                .background(WidgetGold)
                .cornerRadius(18.dp)
                .padding(horizontal = 10.dp, vertical = 5.dp),
        )
        Spacer(modifier = GlanceModifier.height(5.dp))
        Text(
            text = nextPrayer?.time.orEmpty(),
            style = TextStyle(
                color = WidgetText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
        )
    }
}
