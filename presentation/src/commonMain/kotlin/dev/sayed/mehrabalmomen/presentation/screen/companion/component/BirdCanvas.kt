package dev.sayed.mehrabalmomen.presentation.screen.companion.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.domain.model.companion.CompanionMood
import kotlinx.coroutines.delay
import kotlin.random.Random

@Composable
fun BirdCanvas(
    mood: CompanionMood,
    modifier: Modifier = Modifier,
    isLaughing: Boolean = false,
    isDoingTasbih: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "bird_living")

    // Animations as State objects (No 'by' to avoid recomposition)
    val hoverYState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "hover_y"
    )

    val breathingScaleState = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathing"
    )

    val wingCycleState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isLaughing) 200 else 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wing_cycle"
    )

    var lookX by remember { mutableStateOf(0f) }
    var lookY by remember { mutableStateOf(0f) }
    var isBlinking by remember { mutableStateOf(false) }

    LaunchedEffect(isLaughing) {
        if (!isLaughing) {
            while (true) {
                delay(Random.nextLong(6000, 12000))
                isBlinking = true
                delay(120)
                lookX = Random.nextInt(-4, 4).toFloat()
                lookY = Random.nextInt(-2, 2).toFloat()
                isBlinking = false
                delay(150)
            }
        }
    }

    val laughWobbleState = infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laugh_wobble"
    )
    
    val laughBounceState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(180, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laugh_bounce"
    )

    val laughSquashXState = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laugh_squash_x"
    )
    val laughSquashYState = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.88f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laugh_squash_y"
    )

    val laughBeakOpenState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laugh_beak"
    )

    val laughBlushAlphaState = infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(300, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laugh_blush"
    )

    val subhaMoveState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "subha_move"
    )

    val colorHappy = Color(0xFFFFD54F)
    val colorSad = Color(0xFF90A4AE)
    val colorExcited = Color(0xFFFF7043)
    val colorThinking = Color(0xFF81C784)
    val colorSleeping = Color(0xFFBA68C8)
    
    val baseColor = when (mood) {
        CompanionMood.HAPPY -> colorHappy
        CompanionMood.SAD -> colorSad
        CompanionMood.EXCITED -> colorExcited
        CompanionMood.THINKING -> colorThinking
        CompanionMood.SLEEPING -> colorSleeping
    }

    Canvas(
        modifier = modifier
            .size(80.dp)
            .graphicsLayer {
                if (isLaughing) {
                    rotationZ = laughWobbleState.value
                    translationY = laughBounceState.value
                    scaleX = laughSquashXState.value
                    scaleY = laughSquashYState.value
                }
            }
            .offset { 
                IntOffset(0, hoverYState.value.dp.roundToPx()) 
            }
    ) {
        val width = size.width
        val height = size.height

        val tailPath = Path().apply {
            moveTo(width * 0.5f, height * 0.75f)
            quadraticTo(width * 0.35f, height * 0.85f, width * 0.3f, height * 0.95f)
            lineTo(width * 0.7f, height * 0.95f)
            quadraticTo(width * 0.65f, height * 0.85f, width * 0.5f, height * 0.75f)
        }
        drawPath(tailPath, baseColor.copy(alpha = 0.6f))

        val wingY = height * 0.45f
        
        fun drawNaturalWing(isRight: Boolean) {
            val scaleX = if (isRight) -1f else 1f
            val wingFold = wingCycleState.value
            val rotation = if (isRight) -20f * wingFold else 20f * wingFold
            val wingScaleY = 0.7f + (0.3f * (1 - wingFold))
            
            withTransform({
                scale(scaleX, 1f, Offset(width / 2, height / 2))
                rotate(rotation, Offset(width * 0.22f, wingY))
                scale(1f, wingScaleY, Offset(width * 0.22f, wingY))
            }) {
                val wingPath = Path().apply {
                    moveTo(width * 0.22f, wingY)
                    quadraticTo(width * 0.02f, wingY - height * 0.05f, width * 0.12f, wingY - height * 0.2f)
                    quadraticTo(width * 0.18f, wingY - height * 0.18f, width * 0.22f, wingY)
                    close()
                }
                drawPath(wingPath, baseColor)
                drawPath(wingPath, Color.Black.copy(alpha = 0.05f), style = Stroke(width = 1f))
            }
        }
        drawNaturalWing(false)
        drawNaturalWing(true)

        val feetY = height * 0.82f
        val footColor = Color(0xFF424242)
        drawPath(Path().apply {
            moveTo(width * 0.42f, feetY); lineTo(width * 0.38f, feetY + height * 0.1f)
            moveTo(width * 0.42f, feetY); lineTo(width * 0.42f, feetY + height * 0.12f)
            moveTo(width * 0.42f, feetY); lineTo(width * 0.46f, feetY + height * 0.1f)
        }, footColor, style = Stroke(width = 4f, cap = StrokeCap.Round))
        
        drawPath(Path().apply {
            moveTo(width * 0.58f, feetY); lineTo(width * 0.54f, feetY + height * 0.1f)
            moveTo(width * 0.58f, feetY); lineTo(width * 0.58f, feetY + height * 0.12f)
            moveTo(width * 0.58f, feetY); lineTo(width * 0.62f, feetY + height * 0.1f)
        }, footColor, style = Stroke(width = 4f, cap = StrokeCap.Round))

        val bodyBrush = Brush.radialGradient(
            colors = listOf(baseColor.copy(alpha = 0.9f), baseColor),
            center = Offset(width * 0.45f, height * 0.4f),
            radius = width * 0.4f
        )
        
        withTransform({
            scale(1f, breathingScaleState.value, Offset(width * 0.5f, height * 0.8f))
        }) {
            drawRoundRect(
                brush = bodyBrush,
                topLeft = Offset(width * 0.22f, height * 0.25f),
                size = Size(width * 0.56f, height * 0.6f),
                cornerRadius = CornerRadius(width * 0.28f, width * 0.28f)
            )
            
            drawOval(
                color = Color.White.copy(alpha = 0.4f),
                topLeft = Offset(width * 0.35f, height * 0.58f),
                size = Size(width * 0.3f, height * 0.22f)
            )
            
            if (mood == CompanionMood.HAPPY || mood == CompanionMood.EXCITED || isLaughing) {
                val blushAlpha = if (isLaughing) laughBlushAlphaState.value else 0.6f
                drawCircle(Color(0xFFFFB7B2), width * 0.05f, Offset(width * 0.32f, height * 0.45f), alpha = blushAlpha)
                drawCircle(Color(0xFFFFB7B2), width * 0.05f, Offset(width * 0.68f, height * 0.45f), alpha = blushAlpha)
            }
        }

        val eyeY = height * 0.4f
        val eyeSize = width * 0.08f
        val currentLookX = if (isLaughing) 0f else lookX.dp.toPx()
        val currentLookY = if (isLaughing) 0f else (lookY - 2).dp.toPx()

        if (isLaughing) {
            drawArc(
                color = Color.Black,
                startAngle = 0f,
                sweepAngle = -180f,
                useCenter = false,
                topLeft = Offset(width * 0.36f, eyeY - height * 0.02f),
                size = Size(width * 0.12f, height * 0.06f),
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )
            drawArc(
                color = Color.Black,
                startAngle = 0f,
                sweepAngle = -180f,
                useCenter = false,
                topLeft = Offset(width * 0.52f, eyeY - height * 0.02f),
                size = Size(width * 0.12f, height * 0.06f),
                style = Stroke(width = 5f, cap = StrokeCap.Round)
            )
        } else {
            fun drawEye(centerX: Float) {
                drawCircle(Color.Black, radius = eyeSize, center = Offset(centerX, eyeY))
                if (!isBlinking) {
                    drawCircle(Color.White, radius = eyeSize * 0.35f, center = Offset(centerX + currentLookX, eyeY + currentLookY))
                    drawCircle(Color.White.copy(alpha = 0.5f), radius = eyeSize * 0.15f, center = Offset(centerX + eyeSize * 0.3f, eyeY - eyeSize * 0.3f))
                }
            }
            drawEye(width * 0.42f)
            drawEye(width * 0.58f)

            if (isBlinking) {
                drawRect(baseColor, topLeft = Offset(width * 0.3f, eyeY - eyeSize), size = Size(width * 0.4f, eyeSize * 2.2f))
            }
        }

        if (isDoingTasbih) {
            val subhaX = width * 0.68f
            val subhaY = feetY + height * 0.05f + subhaMoveState.value
            
            drawLine(
                Color.DarkGray, 
                Offset(width * 0.58f, feetY + height * 0.05f), 
                Offset(subhaX, subhaY + 12.dp.toPx()), 
                strokeWidth = 2f
            )
            
            repeat(5) { i ->
                drawCircle(
                    color = Color(0xFF795548),
                    radius = 3.dp.toPx(),
                    center = Offset(subhaX, subhaY + (i * 7).dp.toPx())
                )
            }
        }

        val beakPath = Path().apply {
            val beakFactor = if (isLaughing) laughBeakOpenState.value else 0f
            moveTo(width * 0.45f, height * 0.48f)
            lineTo(width * 0.55f, height * 0.48f)
            lineTo(width * 0.5f, height * (0.62f + beakFactor * 0.05f))
            close()
        }
        drawPath(beakPath, Color(0xFFFF9100))

        drawPath(Path().apply {
            moveTo(width * 0.5f, height * 0.25f)
            quadraticTo(width * 0.55f, height * 0.05f, width * 0.65f, height * 0.12f)
            moveTo(width * 0.5f, height * 0.25f)
            quadraticTo(width * 0.45f, height * 0.05f, width * 0.35f, height * 0.15f)
        }, baseColor, style = Stroke(width = 5f, cap = StrokeCap.Round))
    }
}
