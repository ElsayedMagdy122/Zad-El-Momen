package dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.layout.Alignment
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetUiState

@Composable
internal fun MediumPrayerWidget(
    state: PrayerWidgetUiState,
    contentDescription: String,
) {
    Row(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CountdownCircle(
            state = state,
            modifier = GlanceModifier.size(94.dp),
            contentDescription = contentDescription,
        )
        Spacer(modifier = GlanceModifier.width(8.dp))
        PrayerTimeline(
            state = state,
            modifier = GlanceModifier
                .fillMaxWidth()
                .height(96.dp),
        )
    }
}
