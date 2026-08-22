package dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetUiState

@Composable
internal fun MediumAltPrayerWidget(state: PrayerWidgetUiState) {
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(6.dp)
            .background(WidgetWhite)
            .cornerRadius(20.dp)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PrayerList(
            state = state,
            modifier = GlanceModifier.width(168.dp),
        )
        Spacer(modifier = GlanceModifier.width(8.dp))
        Box(
            modifier = GlanceModifier
                .width(1.dp)
                .height(148.dp)
                .background(WidgetDivider),
        ) {}
        Spacer(modifier = GlanceModifier.width(8.dp))
        NextPrayerPanel(
            state = state,
            modifier = GlanceModifier.defaultWeight(),
        )
    }
}
