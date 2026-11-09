package dev.sayed.mehrabalmomen.presentation.screen.recitation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.data.recitation.data.QuranVerse
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.recitation.WordStatus
import dev.sayed.mehrabalmomen.presentation.screen.recitation.cleanWordText
import dev.sayed.mehrabalmomen.presentation.screen.recitation.toArabicNumerals

/**
 * Renders the entire Surah in an authentic, justified Mus'haf flow using Hafs calligraphy and 3-color status protocol.
 */
@Composable
fun Tasmee3MushafSection(
    verses: List<QuranVerse>,
    wordStatuses: Map<Int, WordStatus>,
    activeCursor: Int,
    onWordClick: (globalWordIdx: Int, wordText: String) -> Unit = { _, _ -> }
) {
    val hafsFont = remember { FontFamily(Font(R.font.hafs)) }
    val primaryColor = Theme.color.primary.primary

    val annotatedMushafText = remember(verses, wordStatuses, activeCursor) {
        buildAnnotatedString {
            verses.forEach { verse ->
                verse.displayWords.forEachIndexed { wordIdx, word ->
                    val cleanWord = cleanWordText(word)
                    if (cleanWord.isEmpty()) return@forEachIndexed

                    val globalIdx = verse.globalStartIndex + wordIdx
                    val status = wordStatuses[globalIdx] ?: WordStatus.UNSPOKEN

                    val isRevealed = status != WordStatus.UNSPOKEN

                    pushStringAnnotation(tag = "WORD_CLICK", annotation = "$globalIdx|$cleanWord")

                    if (isRevealed) {
                        val wordColor = when (status) {
                            WordStatus.GREEN -> Color(0xFF2E7D32) // 🟢 Correctly recited
                            WordStatus.YELLOW -> Color(0xFFFBC02D) // 🟡 Tajweed warning (Bright Yellow)
                            WordStatus.RED -> Color(0xFFC62828) // 🔴 Skipped / Mispronounced
                            WordStatus.NEUTRAL -> primaryColor
                            WordStatus.UNSPOKEN -> Color.Transparent
                        }
                        withStyle(
                            SpanStyle(
                                color = wordColor,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(cleanWord)
                        }
                    } else {
                        // Unspoken word placeholder: subtle, elegant dots in Hafs proportions
                        withStyle(
                            SpanStyle(
                                color = Color.LightGray.copy(alpha = 0.55f),
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append("••••")
                        }
                    }
                    pop()
                    append(" ")
                }

                // Ayah end symbol with Arabic numerals
                withStyle(
                    SpanStyle(
                        color = primaryColor,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("${toArabicNumerals(verse.ayah)} ")
                }
            }
        }
    }

    ClickableText(
        text = annotatedMushafText,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        style = TextStyle(
            fontSize = 24.sp,
            fontFamily = hafsFont,
            lineHeight = 52.sp,
            textAlign = TextAlign.Justify,
            textDirection = TextDirection.Rtl,
            platformStyle = PlatformTextStyle(includeFontPadding = false)
        ),
        onClick = { offset ->
            annotatedMushafText.getStringAnnotations("WORD_CLICK", offset, offset)
                .firstOrNull()?.let { annotation ->
                    val parts = annotation.item.split("|")
                    if (parts.size >= 2) {
                        val globalWordIdx = parts[0].toIntOrNull() ?: return@let
                        val wordText = parts[1]
                        onWordClick(globalWordIdx, wordText)
                    }
                }
        }
    )
}
