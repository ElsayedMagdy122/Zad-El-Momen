package dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetUiState

@Composable
internal fun PrayerTimeline(
    state: PrayerWidgetUiState,
    modifier: GlanceModifier,
) {
    val upcomingIndex = state.upcomingPrayerIndex()

    Column(
        modifier = modifier
            .background(WidgetWhite)
            .cornerRadius(16.dp)
            .padding(horizontal = 7.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            state.prayers.forEachIndexed { index, _ ->
                Box(
                    modifier = GlanceModifier
                        .defaultWeight()
                        .height(3.dp)
                        .background(
                            if (index <= upcomingIndex) WidgetGold else WidgetLightGold,
                        ),
                ) {}
            }
        }
        Spacer(modifier = GlanceModifier.height(5.dp))
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            state.prayers.forEach { prayer ->
                PrayerTimelineItem(
                    prayer = prayer,
                    modifier = GlanceModifier.defaultWeight(),
                )
            }
        }
    }
}
