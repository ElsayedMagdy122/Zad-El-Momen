package dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.RecitationCompletionResult
import org.jetbrains.compose.resources.painterResource

/**
 * Authentic Completion Bottom Sheet built strictly following the Zad El Momen Design System,
 * using BottomSheetDs, PrimaryButton, DailyProgressCard style statistics, and Theme colors.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecitationCompletionBottomSheet(
    result: RecitationCompletionResult,
    surahName: String,
    onRestart: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appLocale = LocalAppLocale.current

    var isAnimated by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isAnimated = true
    }

    val animatedAccuracy by animateIntAsState(
        targetValue = if (isAnimated) result.accuracyPercentage else 0,
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "accuracyCountAnim"
    )

    val animatedProgress by animateFloatAsState(
        targetValue = if (isAnimated) (result.accuracyPercentage / 100f).coerceIn(0f, 1f) else 0f,
        animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing),
        label = "accuracySweepAnim"
    )

    BottomSheetDs(
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = localizedString(Res.string.recitation_result_title),
                        style = Theme.textStyle.title.medium,
                        color = Theme.color.primary.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = localizedString(Res.string.recitation_result_surah_prefix, surahName),
                        style = Theme.textStyle.body.small,
                        color = Theme.color.secondary.shadeSecondary
                    )
                }
            }

            // Progress & Statistics Card (identical in design & structure to DailyProgressCard)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Theme.color.surfaces.surfaceLow)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 3 Metric Columns
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 1. Correct Words
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(Res.drawable.check),
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.correctWords.toString().toLocalizedDigits(appLocale),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Theme.color.primary.primary
                            )
                            Text(
                                text = localizedString(Res.string.recitation_result_correct),
                                fontSize = 11.sp,
                                color = Theme.color.secondary.shadeSecondary
                            )
                        }

                        // 2. Error / Missed Words
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_close_circle),
                                contentDescription = null,
                                tint = Theme.color.semantic.error,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.errorWords.toString().toLocalizedDigits(appLocale),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Theme.color.primary.primary
                            )
                            Text(
                                text = localizedString(Res.string.recitation_result_errors),
                                fontSize = 11.sp,
                                color = Theme.color.secondary.shadeSecondary
                            )
                        }

                        // 3. Total Words
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_quran02),
                                contentDescription = null,
                                tint = Theme.color.secondary.secondary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = result.totalWords.toString().toLocalizedDigits(appLocale),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Theme.color.primary.primary
                            )
                            Text(
                                text = localizedString(Res.string.recitation_result_words),
                                fontSize = 11.sp,
                                color = Theme.color.secondary.shadeSecondary
                            )
                        }
                    }

                    // Circular Accuracy Percentage Indicator
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.size(64.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { animatedProgress },
                                modifier = Modifier.fillMaxSize(),
                                color = if (result.accuracyPercentage >= 85) Color(0xFF4CAF50) else Theme.color.primary.primary,
                                trackColor = Theme.color.surfaces.surfaceHigh,
                                strokeWidth = 5.dp
                            )
                            Text(
                                text = "${animatedAccuracy.toString().toLocalizedDigits(appLocale)}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Theme.color.primary.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = localizedString(Res.string.recitation_result_accuracy),
                            fontSize = 10.sp,
                            color = Theme.color.secondary.shadeSecondary
                        )
                    }
                }
            }

            // Recitation Colors Meaning Guide
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Theme.color.surfaces.surfaceLow)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = localizedString(Res.string.recitation_result_guide_title),
                    style = Theme.textStyle.label.small,
                    color = Theme.color.primary.primary,
                    fontWeight = FontWeight.Bold
                )

                // Green meaning
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF4CAF50))
                    )
                    Text(
                        text = localizedString(Res.string.recitation_result_green_meaning),
                        style = Theme.textStyle.body.small,
                        color = Theme.color.secondary.shadeSecondary
                    )
                }

                // Red meaning
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(Theme.color.semantic.error)
                    )
                    Text(
                        text = localizedString(Res.string.recitation_result_red_meaning),
                        style = Theme.textStyle.body.small,
                        color = Theme.color.secondary.shadeSecondary
                    )
                }
            }

            // Appreciation & Rating Message Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Theme.color.surfaces.surfaceLow)
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Theme.color.surfaces.surfaceHigh),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_star_rate),
                        contentDescription = null,
                        tint = Theme.color.secondary.secondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    val ratingTitle = result.ratingTitleRes?.let { localizedString(it) } ?: result.ratingTitle
                    val ratingMessage = result.ratingMessageRes?.let { localizedString(it) } ?: result.ratingMessage

                    Text(
                        text = ratingTitle,
                        style = Theme.textStyle.label.medium,
                        color = Theme.color.primary.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = ratingMessage,
                        style = Theme.textStyle.body.small,
                        color = Theme.color.secondary.shadeSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Primary Restart Button
            PrimaryButton(
                text = localizedString(Res.string.recitation_result_restart),
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth()
            )

            // Secondary Dismiss Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Theme.color.surfaces.surfaceLow)
                    .clickable(onClick = onDismiss)
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = localizedString(Res.string.recitation_result_review),
                    style = Theme.textStyle.label.medium,
                    color = Theme.color.secondary.shadeSecondary
                )
            }
        }
    }
}
