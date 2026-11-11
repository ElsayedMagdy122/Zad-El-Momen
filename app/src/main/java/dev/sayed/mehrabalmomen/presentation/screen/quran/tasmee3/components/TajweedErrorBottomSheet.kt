package dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.data.recitation.tajweed.ReciterErrorSeverity
import dev.sayed.mehrabalmomen.data.recitation.tajweed.ReciterMistake
import dev.sayed.mehrabalmomen.design_system.theme.Theme

/**
 * Islamic Modal Bottom Sheet presenting detailed Tajweed, Tashkeel, or Pronunciation feedback for a word.
 * Clean Architecture counterpart of TajweedErrorSheet in codeflutter.md.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TajweedErrorBottomSheet(
    wordText: String,
    mistakes: List<ReciterMistake>,
    onDismiss: () -> Unit
) {
    val modalBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalBottomSheetState,
        containerColor = Theme.color.surfaces.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.Gray.copy(alpha = 0.35f))
            )
        },
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = localizedString(R.string.recitation_feedback_on_word, wordText),
                    style = Theme.textStyle.title.large.copy(
                        fontWeight = FontWeight.Bold,
                        color = Theme.color.primary.primary,
                        fontSize = 20.sp
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // List of mistakes for this word
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                mistakes.forEach { mistake ->
                    MistakeCard(mistake = mistake)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Got it / Close button
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Theme.color.primary.primary,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = localizedString(R.string.recitation_feedback_close),
                    style = Theme.textStyle.label.medium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun MistakeCard(mistake: ReciterMistake) {
    val (iconColor, bgColor) = when (mistake.severity) {
        ReciterErrorSeverity.TAJWEED -> Color(0xFFFBC02D) to Color(0xFFFFF9C4).copy(alpha = 0.35f)
        ReciterErrorSeverity.TASHKEEL -> Color(0xFFFB8C00) to Color(0xFFFFE0B2).copy(alpha = 0.35f)
        ReciterErrorSeverity.PHONETIC -> Color(0xFFE53935) to Color(0xFFFFCDD2).copy(alpha = 0.35f)
    }

    val title = when (mistake) {
        is ReciterMistake.UnderheldDuration -> {
            val ruleName = localizedString(mistake.ruleType.titleResId)
            localizedString(
                R.string.tajweed_error_underheld,
                ruleName,
                mistake.actualSeconds,
                mistake.expectedSeconds
            )
        }
        is ReciterMistake.OverheldDuration -> {
            val ruleName = localizedString(mistake.ruleType.titleResId)
            localizedString(
                R.string.tajweed_error_overheld,
                ruleName,
                mistake.actualSeconds,
                mistake.maxAllowedSeconds
            )
        }
        is ReciterMistake.TashkeelMismatch -> {
            localizedString(
                R.string.tajweed_error_tashkeel,
                mistake.expectedPhoneme,
                mistake.actualPhoneme
            )
        }
        is ReciterMistake.LetterOmitted -> {
            localizedString(R.string.tajweed_error_omission, mistake.omittedLetter)
        }
        is ReciterMistake.LetterSubstituted -> {
            localizedString(
                R.string.tajweed_error_substitution,
                mistake.expectedLetter,
                mistake.actualLetter
            )
        }
    }

    val advice = localizedString(mistake.adviceResId)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = iconColor.copy(alpha = 0.35f),
                shape = RoundedCornerShape(14.dp)
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (mistake.severity) {
                        ReciterErrorSeverity.TAJWEED -> "⏱️"
                        ReciterErrorSeverity.TASHKEEL -> "✏️"
                        ReciterErrorSeverity.PHONETIC -> "⚠️"
                    },
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = Theme.textStyle.body.medium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Theme.color.secondary.secondaryText,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = advice,
                    style = Theme.textStyle.body.small.copy(
                        color = Theme.color.semantic.shadeTertiary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                )
            }
        }
    }
}
