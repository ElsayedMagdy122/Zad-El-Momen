package dev.sayed.mehrabalmomen.presentation.screen.companion

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.sayed.mehrabalmomen.presentation.screen.companion.component.BirdCanvas
import dev.sayed.mehrabalmomen.presentation.screen.companion.component.DialogueBubble
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.random.Random

@Stable
class ParticleHolder {
    val particles = ArrayList<FeatherData>()
}

@Composable
fun CompanionOverlay(
    viewModel: CompanionViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    
    if (!state.isVisible) return

    val scope = rememberCoroutineScope()
    
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var currentRotationY by remember { mutableStateOf(0f) }
    
    val particleHolder = remember { ParticleHolder() }
    var ticker by remember { mutableLongStateOf(0L) }

    LaunchedEffect(Unit) {
        val startTime = System.currentTimeMillis()
        while (true) {
            withFrameNanos { frameTime ->
                val particles = particleHolder.particles
                if (state.isLaughing && particles.size < 40 && Random.nextFloat() > 0.8f) {
                    particles.add(FeatherData(
                        x = Random.nextFloat() * 20 - 10,
                        y = 0f,
                        vx = Random.nextFloat() * 6 - 3f,
                        vy = Random.nextFloat() * -5 - 2f, 
                        life = 1f,
                        rot = Random.nextFloat() * 360f,
                        sway = Random.nextFloat() * 10f,
                        isBehind = Random.nextBoolean()
                    ))
                }

                val t = (System.currentTimeMillis() - startTime) / 1000f
                val it = particles.iterator()
                while (it.hasNext()) {
                    val f = it.next()
                    f.x += f.vx + kotlin.math.sin(t * 8f + f.sway) * 1.5f
                    f.y += f.vy
                    f.vy += 0.4f 
                    f.life -= if (state.isLaughing) 0.02f else 0.05f 
                    f.rot += 6f
                    
                    if (f.life <= 0 || f.y > 60f || f.y < -100f || f.x > 60f || f.x < -60f) {
                        it.remove()
                    }
                }
                ticker = frameTime
            }
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(Random.nextLong(20000, 45000)) 
            if (state.isLaughing) continue

            val targetX = Random.nextInt(-40, 5).toFloat()
            val targetY = Random.nextInt(-20, 10).toFloat()
            val jumpHeight = -80f
            
            val startX = offsetX.value
            val startY = offsetY.value
            currentRotationY = if (targetX < startX) 180f else 0f
            
            animate(0f, 1f, animationSpec = tween(1200, easing = LinearOutSlowInEasing)) { progress, _ ->
                scope.launch {
                    offsetX.snapTo(startX + (targetX - startX) * progress)
                    val currentY = startY + (targetY - startY) * progress + (4 * jumpHeight * progress * (progress - 1))
                    offsetY.snapTo(currentY)
                }
            }
            viewModel.refreshDialogue()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .offset { IntOffset(offsetX.value.dp.roundToPx(), offsetY.value.dp.roundToPx()) }
                .graphicsLayer { rotationY = currentRotationY }
                .padding(bottom = 90.dp) // Lifted above the bottom navigation bar
        ) {
            AnimatedVisibility(
                visible = state.dialogue != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                state.dialogue?.let {
                    DialogueBubble(
                        text = it.asString(),
                        modifier = Modifier
                            .widthIn(max = 220.dp)
                            .graphicsLayer { rotationY = -currentRotationY }
                            .padding(bottom = 0.dp) // Tighten gap to bird
                    )
                }
            }
            
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                // Pass ticker as lambda to prevent recomposition of CompanionScreen
                ParticleCanvas(particleHolder, { ticker }, isBehind = true)

                BirdCanvas(
                    mood = state.mood,
                    isLaughing = state.isLaughing,
                    isDoingTasbih = state.isDoingTasbih,
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = { viewModel.onInteract() }
                            )
                        }
                )

                ParticleCanvas(particleHolder, { ticker }, isBehind = false)
            }
        }
    }
}

@Composable
private fun ParticleCanvas(
    holder: ParticleHolder, 
    tickerProvider: () -> Long, 
    isBehind: Boolean
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        // Read ticker inside DrawScope to bypass Recomposition phase
        val _dep = tickerProvider() 
        holder.particles.forEach { f ->
            if (f.isBehind == isBehind) {
                val fx = f.x.dp.toPx()
                val fy = f.y.dp.toPx()
                withTransform({
                    translate(size.width / 2 + fx, size.height / 2 + fy)
                    rotate(f.rot)
                }) {
                    val p = Path().apply {
                        moveTo(0f, 0f)
                        quadraticTo(3.dp.toPx(), 6.dp.toPx(), 0f, 12.dp.toPx())
                        quadraticTo(-3.dp.toPx(), 6.dp.toPx(), 0f, 0f)
                    }
                    drawPath(p, Color(0xFFFFF9C4).copy(alpha = f.life * 0.6f))
                }
            }
        }
    }
}

class FeatherData(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var life: Float,
    var rot: Float,
    var sway: Float,
    val isBehind: Boolean
)
