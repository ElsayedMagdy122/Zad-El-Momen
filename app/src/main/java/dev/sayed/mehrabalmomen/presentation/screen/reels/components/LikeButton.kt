package dev.sayed.mehrabalmomen.presentation.screen.reels.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import kotlinx.coroutines.launch
import androidx.compose.foundation.Canvas
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import dev.sayed.mehrabalmomen.R

@Composable
fun LikeButton(
    count: Int,
    isLiked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        HeartIcon(isLiked = isLiked, onClick = onClick)
        Text(
            text = formatCount(count),
            style = Theme.textStyle.body.medium,
            color = Color.White,
        )
    }
}


@Composable
fun HeartIcon(
    isLiked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = 32.dp,
    icon: Painter = painterResource(R.drawable.ic_heart)
) {
    var pressed by remember { mutableStateOf(false) }
    var isFirstComposition by remember { mutableStateOf(true) }

    val scale by animateFloatAsState(
        targetValue = if (pressed) 1.3f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        finishedListener = { pressed = false },
    )

    val animatedTint by animateColorAsState(
        targetValue = if (isLiked) Theme.color.semantic.error else Theme.color.semantic.shadeTertiary
    )

    val alpha = remember { Animatable(0f) }

    val xOffset1 = remember { Animatable(0f) }
    val yOffset1 = remember { Animatable(0f) }

    val xOffset2 = remember { Animatable(0f) }
    val yOffset2 = remember { Animatable(0f) }

    val xOffset3 = remember { Animatable(0f) }
    val yOffset3 = remember { Animatable(0f) }

    LaunchedEffect(isLiked) {
        if (isFirstComposition){
            isFirstComposition = false
            return@LaunchedEffect
        }

        if (isLiked) {
            xOffset1.snapTo(0f); yOffset1.snapTo(0f)
            xOffset2.snapTo(0f); yOffset2.snapTo(0f)
            xOffset3.snapTo(0f); yOffset3.snapTo(0f)
            alpha.snapTo(1f)
            launch {
                launch { xOffset1.animateTo(-55f, tween(1400)) }
                yOffset1.animateTo(-140f, tween(1600))
            }
            launch {
                launch { xOffset2.animateTo(10f, tween(1400)) }
                yOffset2.animateTo(-170f, tween(1600, delayMillis = 80))
            }
            launch {
                launch { xOffset3.animateTo(50f, tween(1400)) }
                yOffset3.animateTo(-120f, tween(1600, delayMillis = 160))
            }
            launch {
                alpha.animateTo(0f, tween(800, delayMillis = 900))
            }
        } else {
            xOffset1.stop(); yOffset1.stop()
            xOffset2.stop(); yOffset2.stop()
            xOffset3.stop(); yOffset3.stop()
            alpha.stop()

            xOffset1.snapTo(0f); yOffset1.snapTo(0f)
            xOffset2.snapTo(0f); yOffset2.snapTo(0f)
            xOffset3.snapTo(0f); yOffset3.snapTo(0f)
            alpha.snapTo(0f)
        }
    }

    Box(modifier.clickAnimation {
        pressed = true
        onClick()
    }) {

        // Heart 1 — large, flies upper-left
        HeartGradient(
            colors = listOf(
                Theme.color.semantic.error.copy(alpha = 0.9f),
                Theme.color.semantic.error.copy(alpha = 0.6f),
                Theme.color.semantic.error.copy(alpha = 0.2f),
            ),
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.Center)
                .alpha(alpha.value)
                .graphicsLayer {
                    translationX = xOffset1.value.dp.toPx()
                    translationY = yOffset1.value.dp.toPx()
                }
        )

        // Heart 2 — medium, flies straight up
        HeartGradient(
            colors = listOf(
                Theme.color.semantic.error.copy(alpha = 0.9f),
                Theme.color.semantic.error.copy(alpha = 0.6f),
                Theme.color.semantic.error.copy(alpha = 0.2f),
            ),
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.Center)
                .alpha(alpha.value)
                .graphicsLayer {
                    translationX = xOffset2.value.dp.toPx()
                    translationY = yOffset2.value.dp.toPx()
                }
        )

        // Heart 3 — small, flies upper-right
        HeartGradient(
            colors = listOf(
                Theme.color.semantic.error.copy(alpha = 0.9f),
                Theme.color.semantic.error.copy(alpha = 0.6f),
                Theme.color.semantic.error.copy(alpha = 0.2f),
            ),
            modifier = Modifier
                .size(12.dp)
                .align(Alignment.Center)
                .alpha(alpha.value)
                .graphicsLayer {
                    translationX = xOffset3.value.dp.toPx()
                    translationY = yOffset3.value.dp.toPx()
                }
        )
        Icon(
            painter = icon,
            contentDescription = null,
            tint = animatedTint,
            modifier = Modifier
                .size(iconSize)
                .scale(scale)
                .align(Alignment.Center),
        )
    }
}

@Composable
fun HeartGradient(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(
        Theme.color.semantic.error,
        Theme.color.semantic.error,
    ),
) {
    Canvas(
        modifier = modifier
    ) {
        val scaleX = size.width / 20f
        val scaleY = size.height / 19.35f

        val brush = Brush.verticalGradient(
            colors = colors,
            startY = 0f,
            endY = size.height
        )

        val path = Path().apply {
            moveTo(10f * scaleX, 18.35f * scaleY)

            lineTo(8.55f * scaleX, 17.05f * scaleY)

            cubicTo(
                6.867f * scaleX, 15.533f * scaleY,
                5.475f * scaleX, 14.225f * scaleY,
                4.375f * scaleX, 13.125f * scaleY
            )

            cubicTo(
                3.275f * scaleX, 12.025f * scaleY,
                2.4f * scaleX, 11.038f * scaleY,
                1.75f * scaleX, 10.163f * scaleY
            )

            cubicTo(
                1.1f * scaleX, 9.288f * scaleY,
                0.646f * scaleX, 8.483f * scaleY,
                0.387f * scaleX, 7.75f * scaleY
            )

            cubicTo(
                0.129f * scaleX, 7.017f * scaleY,
                0f * scaleX, 6.267f * scaleY,
                0f * scaleX, 5.5f * scaleY
            )

            cubicTo(
                0f * scaleX, 3.933f * scaleY,
                0.525f * scaleX, 2.625f * scaleY,
                1.575f * scaleX, 1.575f * scaleY
            )

            cubicTo(
                2.625f * scaleX, 0.525f * scaleY,
                3.933f * scaleX, 0f * scaleY,
                5.5f * scaleX, 0f * scaleY
            )

            cubicTo(
                6.367f * scaleX, 0f * scaleY,
                7.192f * scaleX, 0.183f * scaleY,
                7.975f * scaleX, 0.55f * scaleY
            )

            cubicTo(
                8.758f * scaleX, 0.917f * scaleY,
                9.433f * scaleX, 1.433f * scaleY,
                10f * scaleX, 2.1f * scaleY
            )

            cubicTo(
                10.567f * scaleX, 1.433f * scaleY,
                11.242f * scaleX, 0.917f * scaleY,
                12.025f * scaleX, 0.55f * scaleY
            )

            cubicTo(
                12.808f * scaleX, 0.183f * scaleY,
                13.633f * scaleX, 0f * scaleY,
                14.5f * scaleX, 0f * scaleY
            )

            cubicTo(
                16.067f * scaleX, 0f * scaleY,
                17.375f * scaleX, 0.525f * scaleY,
                18.425f * scaleX, 1.575f * scaleY
            )

            cubicTo(
                19.475f * scaleX, 2.625f * scaleY,
                20f * scaleX, 3.933f * scaleY,
                20f * scaleX, 5.5f * scaleY
            )

            cubicTo(
                20f * scaleX, 6.267f * scaleY,
                19.871f * scaleX, 7.017f * scaleY,
                19.612f * scaleX, 7.75f * scaleY
            )

            cubicTo(
                19.354f * scaleX, 8.483f * scaleY,
                18.9f * scaleX, 9.288f * scaleY,
                18.25f * scaleX, 10.163f * scaleY
            )

            cubicTo(
                17.6f * scaleX, 11.038f * scaleY,
                16.725f * scaleX, 12.025f * scaleY,
                15.625f * scaleX, 13.125f * scaleY
            )

            cubicTo(
                14.525f * scaleX, 14.225f * scaleY,
                13.133f * scaleX, 15.533f * scaleY,
                11.45f * scaleX, 17.05f * scaleY
            )

            close()
        }

        drawPath(
            path = path,
            brush = brush
        )
    }
}