package dev.sayed.mehrabalmomen.presentation.screen.calibrate_device.component

import android.graphics.PathMeasure
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin


@Composable
 fun CardFigureAnimation(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedFigure8Component(modifier = Modifier.size(160.dp))
    }
}

@Composable
private fun AnimatedFigure8Component(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val strokeColor = Theme.color.primary.primary
    val circleColor = Theme.color.secondary.secondaryText
    val icon = ImageBitmap.imageResource(id = R.drawable.iphone)
    val imageSizeDp = 40.dp

    val imageSizePx = with(LocalDensity.current) { imageSizeDp.toPx() }
    val fraction by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            val w = size.width
            val h = size.height
            val center = Offset(w / 2, h / 2)
            val scale = w.coerceAtMost(h) * 0.35f

            val path = Path()
            val steps = 200

            for (i in 0..steps) {
                val t = (i.toFloat() / steps) * (2 * Math.PI).toFloat()

                val denom = 1 + sin(t).pow(2)

                val x = (scale * cos(t) / denom) + center.x
                val y = (scale * sin(t) * cos(t) / denom) + center.y

                if (i == 0)
                    path.moveTo(x, y)
                else
                    path.lineTo(x, y)
            }

            drawPath(
                path = path,
                color = strokeColor,
                style = Stroke(width = 4.dp.toPx())
            )

            val pm = PathMeasure(path.asAndroidPath(), false)
            val distance = pm.length * fraction

            val pos = FloatArray(2)
            pm.getPosTan(distance, pos, null)


            drawCircle(
                //  color = Color(0xFFDAA520),
                color = circleColor,
                radius = 8.dp.toPx(),
                center = Offset(pos[0], pos[1])
            )

//            drawImage(
//                image = icon,
//                srcOffset = IntOffset.Zero,
//                srcSize = IntSize(icon.width, icon.height),
//                dstOffset = IntOffset(
//                    (pos[0] - (imageSizePx / 2)).toInt(),
//                    (pos[1] - (imageSizePx / 2)).toInt()
//                ),
//                dstSize = IntSize(
//                    imageSizePx.toInt(),
//                    imageSizePx.toInt()
//                )
//            )
        }
    }
}
