package dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.background
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetUiState

@Composable
internal fun PrayerList(
    state: PrayerWidgetUiState,
    modifier: GlanceModifier,
) {
    Column(modifier = modifier) {
        state.prayers.forEachIndexed { index, prayer ->
            PrayerListItem(prayer)
            if (index < state.prayers.lastIndex) {
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(WidgetDivider),
                ) {}
            }
        }
    }
}
