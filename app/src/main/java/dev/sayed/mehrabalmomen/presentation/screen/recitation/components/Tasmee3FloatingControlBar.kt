package dev.sayed.mehrabalmomen.presentation.screen.recitation.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme

/**
 * Floating Control Bar with Record Toggle, Pulse Animation, Tajweed Switch, and ASR Live Transcript.
 */
@Composable
fun Tasmee3FloatingControlBar(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    isRecording: Boolean,
    recognizedText: String,
    lastEvaluatedInfo: String,
    isTajweed: Boolean,
    onToggleTajweed: (Boolean) -> Unit,
    onToggleRecord: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.22f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Theme.color.surfaces.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Record Status Badge & Live Cursor
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isLoading -> Theme.color.primary.shadePrimary
                                    isRecording -> Color(0xFFC62828)
                                    else -> Theme.color.primary.primary
                                }
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            isLoading -> "جاري التهيئة..."
                            isRecording -> "جاري الاستماع والتسميع..."
                            else -> "انقر على الميكروفون للبدء"
                        },
                        style = Theme.textStyle.body.medium,
                        color = when {
                            isRecording -> Color(0xFFC62828)
                            else -> Theme.color.primary.primary
                        },
                        fontWeight = FontWeight.Bold
                    )
                }

                // Record Button with Pulse Effect
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .scale(if (isRecording) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(
                            when {
                                isLoading -> Theme.color.primary.shadePrimary
                                isRecording -> Color(0xFFC62828)
                                else -> Theme.color.primary.primary
                            }
                        )
                        .clickable(enabled = !isLoading) { onToggleRecord() },
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Icon(
                            painter = painterResource(if (isRecording) R.drawable.ic_pause else R.drawable.ic_play),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Tajweed Mode Toggle & Legend
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Theme.color.surfaces.surface)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "تدقيق أحكام التجويد",
                        style = Theme.textStyle.label.small,
                        color = Theme.color.primary.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "🟢 صحيح | 🟡 تنبيه | 🔴 خطأ",
                        style = Theme.textStyle.body.small.copy(fontSize = 11.sp),
                        color = Theme.color.semantic.shadeTertiary
                    )
                }
                Switch(
                    checked = isTajweed,
                    onCheckedChange = onToggleTajweed,
                    modifier = Modifier.scale(0.75f),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Theme.color.primary.primary,
                        checkedTrackColor = Theme.color.primary.shadePrimary
                    )
                )
            }

            // Real-Time Transcript & Evaluation Feedback
            if (recognizedText.isNotBlank() || lastEvaluatedInfo.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Theme.color.surfaces.surface)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    if (recognizedText.isNotBlank()) {
                        Text(
                            text = "الصوت المسموع: $recognizedText",
                            style = Theme.textStyle.body.small.copy(fontSize = 12.sp),
                            color = Color(0xFF1565C0),
                            maxLines = 1
                        )
                    }
                    if (lastEvaluatedInfo.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = lastEvaluatedInfo,
                            style = Theme.textStyle.body.small.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = Theme.color.primary.primary
                        )
                    }
                }
            }
        }
    }
}
