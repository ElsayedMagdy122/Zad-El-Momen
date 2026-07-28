package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.content.Context
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import dev.sayed.mehrabalmomen.R
import kotlin.math.roundToInt

/**
 * Creates the RemoteViews layer containing the calculated prayer countdown ring.
 *
 * @property renderer bitmap renderer used to draw the ring at the launcher's display density.
 */
class PrayerWidgetCountdownRingRemoteViewsFactory(
    private val renderer: PrayerWidgetCountdownRingRenderer = PrayerWidgetCountdownRingRenderer(),
) {
    /**
     * Builds a RemoteViews image layer for the supplied countdown progress.
     *
     * @param context Android context used to resolve density, colors, package, and resources.
     * @param progress elapsed interval progress in the inclusive `0..10000` range.
     * @param contentDescription localized accessibility description for the countdown content.
     * @return RemoteViews containing a density-correct runtime-rendered countdown ring.
     */
    fun create(
        context: Context,
        progress: Int,
        contentDescription: String,
    ): RemoteViews {
        val density = context.resources.displayMetrics.density
        val diameterPixels = dpToPixels(RING_DIAMETER_DP, density).coerceAtLeast(1)
        val strokeWidthPixels = dpToPixels(RING_STROKE_WIDTH_DP, density).toFloat()
        val ringBitmap = renderer.render(
            diameterPixels = diameterPixels,
            strokeWidthPixels = strokeWidthPixels,
            backgroundColor = ContextCompat.getColor(context, R.color.prayer_widget_navy),
            progressColor = ContextCompat.getColor(context, R.color.prayer_widget_gold),
            progress = progress,
        )

        return RemoteViews(context.packageName, R.layout.prayer_widget_countdown_ring_view).apply {
            setImageViewBitmap(R.id.prayer_widget_countdown_ring_image, ringBitmap)
            setContentDescription(R.id.prayer_widget_countdown_ring_image, contentDescription)
        }
    }

    /**
     * Converts a density-independent widget measurement into physical pixels.
     *
     * @param valueDp measurement expressed in density-independent pixels.
     * @param density device display density multiplier.
     * @return nearest physical-pixel measurement for the supplied density.
     */
    private fun dpToPixels(valueDp: Float, density: Float): Int {
        return (valueDp * density).roundToInt()
    }

    private companion object {
        const val RING_DIAMETER_DP = 94f
        const val RING_STROKE_WIDTH_DP = 4f
    }
}
