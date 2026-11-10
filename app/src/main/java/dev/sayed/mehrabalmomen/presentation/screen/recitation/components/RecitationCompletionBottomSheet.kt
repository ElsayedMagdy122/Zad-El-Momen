package dev.sayed.mehrabalmomen.presentation.screen.recitation.components

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.BottomSheetDs
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.screen.recitation.RecitationCompletionResult

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
                        text = "نتيجة التسميع",
                        style = Theme.textStyle.title.medium,
                        color = Theme.color.primary.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "سورة $surahName",
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
                                painter = painterResource(R.drawable.check),
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
                                text = "صحيحة",
                                fontSize = 11.sp,
                                color = Theme.color.secondary.shadeSecondary
                            )
                        }

                        // 2. Error / Missed Words
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close_circle),
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
                                text = "أخطاء",
                                fontSize = 11.sp,
                                color = Theme.color.secondary.shadeSecondary
                            )
                        }

                        // 3. Total Words
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                painter = painterResource(R.drawable.ic_quran02),
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
                                text = "الكلمات",
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
                                progress = { (result.accuracyPercentage / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier.fillMaxSize(),
                                color = if (result.accuracyPercentage >= 85) Color(0xFF4CAF50) else Theme.color.primary.primary,
                                trackColor = Theme.color.surfaces.surfaceHigh,
                                strokeWidth = 5.dp
                            )
                            Text(
                                text = "${result.accuracyPercentage.toString().toLocalizedDigits(appLocale)}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Theme.color.primary.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "نسبة الإتقان",
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
                    text = "دليل ألوان التسميع في المصحف:",
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
                        text = "اللون الأخضر: كلمات قُرِئت بشكل صحيح وبنطق سليم.",
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
                        text = "اللون الأحمر: كلمات حدث فيها خطأ في النطق أو سقطت أثناء التسميع.",
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
                        painter = painterResource(R.drawable.ic_star_rate),
                        contentDescription = null,
                        tint = Theme.color.secondary.secondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = result.ratingTitle,
                        style = Theme.textStyle.label.medium,
                        color = Theme.color.primary.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = result.ratingMessage,
                        style = Theme.textStyle.body.small,
                        color = Theme.color.secondary.shadeSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Primary Restart Button
            PrimaryButton(
                text = "إعادة التسميع من البداية",
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
                    text = "مراجعة الآيات",
                    style = Theme.textStyle.label.medium,
                    color = Theme.color.secondary.shadeSecondary
                )
            }
        }
    }
}
