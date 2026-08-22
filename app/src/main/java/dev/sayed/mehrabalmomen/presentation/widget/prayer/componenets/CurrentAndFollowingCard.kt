package dev.sayed.mehrabalmomen.presentation.widget.prayer.componenets

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.presentation.widget.prayer.PrayerWidgetUiState
import dev.sayed.mehrabalmomen.presentation.widget.prayer.localizedString

@Composable
internal fun CurrentAndFollowingCard(
    state: PrayerWidgetUiState,
    modifier: GlanceModifier,
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .background(WidgetWhite)
            .cornerRadius(16.dp)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CurrentAndFollowingItem(
            prayer = state.currentPrayer(),
            label = state.localizedString(context, R.string.prayer_widget_current),
            modifier = GlanceModifier.defaultWeight(),
        )
        Box(
            modifier = GlanceModifier
                .width(1.dp)
                .height(50.dp)
                .background(WidgetDivider),
        ) {}
        CurrentAndFollowingItem(
            prayer = state.followingPrayer(),
            label = state.localizedString(context, R.string.prayer_widget_next_after),
            modifier = GlanceModifier.defaultWeight(),
        )
    }
}
