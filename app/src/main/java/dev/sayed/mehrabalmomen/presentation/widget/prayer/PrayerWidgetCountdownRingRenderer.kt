package dev.sayed.mehrabalmomen.presentation.widget.prayer

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.graphics.createBitmap

/** Draws a density-correct countdown ring bitmap for the prayer widget host process. */
class PrayerWidgetCountdownRingRenderer {
    /**
     * Renders the navy countdown disc and its calculated clockwise gold progress arc.
     *
     * @param diameterPixels requested square bitmap width and height in physical pixels.
     * @param strokeWidthPixels width of the gold progress stroke in physical pixels.
     * @param backgroundColor color used to fill the circular countdown background.
     * @param progressColor color used to draw elapsed countdown progress.
     * @param progress elapsed interval progress in the inclusive `0..10000` range.
     * @return transparent square bitmap containing the countdown disc and progress arc.
     */
    fun render(
        diameterPixels: Int,
        strokeWidthPixels: Float,
        backgroundColor: Int,
        progressColor: Int,
        progress: Int,
    ): Bitmap {
        val safeDiameter = diameterPixels.coerceAtLeast(1)
        val safeStrokeWidth = strokeWidthPixels.coerceIn(1f, safeDiameter.toFloat())
        val bitmap = createBitmap(safeDiameter, safeDiameter, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val center = safeDiameter / 2f
        val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = backgroundColor
            style = Paint.Style.FILL
        }
        canvas.drawCircle(center, center, center, backgroundPaint)

        val sweepAngle = prayerWidgetCountdownSweepAngle(progress)
        if (sweepAngle > 0f) {
            val progressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = progressColor
                style = Paint.Style.STROKE
                strokeWidth = safeStrokeWidth
                strokeCap = Paint.Cap.ROUND
            }
            val inset = safeStrokeWidth / 2f + 1f
            val arcBounds = RectF(inset, inset, safeDiameter - inset, safeDiameter - inset)
            canvas.drawArc(arcBounds, RING_START_ANGLE_DEGREES, sweepAngle, false, progressPaint)
        }

        return bitmap
    }

    private companion object {
        const val RING_START_ANGLE_DEGREES = -90f
    }
}
