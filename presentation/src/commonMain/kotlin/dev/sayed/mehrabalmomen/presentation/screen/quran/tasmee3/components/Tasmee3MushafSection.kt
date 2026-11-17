package dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.data.recitation.data.QuranVerse
import dev.sayed.mehrabalmomen.data.recitation.tracking.WordStatus
import dev.sayed.mehrabalmomen.designsystem.generated.resources.Res as DesignRes
import dev.sayed.mehrabalmomen.designsystem.generated.resources.hafs
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.cleanWordText
import dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.getAyahEndGlyph
import org.jetbrains.compose.resources.Font

@Composable
fun Tasmee3MushafSection(
    verses: List<QuranVerse>,
    wordStatuses: Map<Int, WordStatus>,
    activeCursor: Int,
    onWordClick: (globalWordIdx: Int, wordText: String) -> Unit = { _, _ -> }
) {
    val font = Font(DesignRes.font.hafs)
    val hafsFont = remember(font) { FontFamily(font) }
    val primaryColor = Theme.color.primary.primary
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val chunkStartIndex = remember(verses) { verses.firstOrNull()?.globalStartIndex ?: 0 }
    val chunkEndIndex = remember(verses) {
        val last = verses.lastOrNull()
        if (last != null) last.globalStartIndex + last.phonemeWords.size else 0
    }

    val chunkStatuses = remember(wordStatuses, chunkStartIndex, chunkEndIndex) {
        wordStatuses.filterKeys { it in chunkStartIndex until chunkEndIndex }
    }

    val isCursorInThisChunk = activeCursor in chunkStartIndex..(chunkEndIndex + 1)
    val localCursor = if (isCursorInThisChunk) activeCursor else -1

    val appearAlpha = remember { Animatable(1f) }
    LaunchedEffect(localCursor) {
        if (localCursor > 0) {
            appearAlpha.snapTo(0.60f)
            appearAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
            )
        }
    }

    val currentAlpha = appearAlpha.value
    val annotatedMushafText = remember(verses, chunkStatuses, localCursor, currentAlpha) {
        buildAnnotatedString {
            verses.forEach { verse ->
                verse.displayWords.forEachIndexed { wordIdx, word ->
                    val cleanWord = cleanWordText(word)
                    if (cleanWord.isEmpty()) return@forEachIndexed

                    val globalIdx = verse.globalStartIndex + wordIdx
                    val status = chunkStatuses[globalIdx] ?: WordStatus.NONE

                    val isRevealed = status != WordStatus.NONE
                    val isJustSpoken = (localCursor > 0 && globalIdx == localCursor - 1)

                    pushStringAnnotation(tag = "WORD_CLICK", annotation = "$globalIdx|$cleanWord")

                    if (isRevealed) {
                        val wordColor = when (status) {
                            WordStatus.GREEN -> Color(0xFF2E7D32)
                            WordStatus.YELLOW -> Color(0xFFD48806)
                            WordStatus.RED -> Color(0xFFD32F2F)
                            else -> primaryColor
                        }
                        val wordAlpha = if (isJustSpoken) currentAlpha else 1f
                        withStyle(
                            SpanStyle(
                                color = wordColor.copy(alpha = wordAlpha),
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(cleanWord)
                        }
                    } else {
                        withStyle(
                            SpanStyle(
                                color = Color(0xFFBDBDBD).copy(alpha = 0.40f),
                                fontWeight = FontWeight.Normal
                            )
                        ) {
                            append("•••")
                        }
                    }
                    pop()
                    append(" ")
                }

                withStyle(
                    SpanStyle(
                        color = primaryColor,
                        fontFamily = hafsFont,
                        fontWeight = FontWeight.Normal
                    )
                ) {
                    append("${getAyahEndGlyph(verse.ayah)} ")
                }
            }
        }
    }

    Text(
        text = annotatedMushafText,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 2.dp)
            .pointerInput(annotatedMushafText) {
                detectTapGestures { pos ->
                    textLayoutResult?.let { layout ->
                        val offset = layout.getOffsetForPosition(pos)
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
                }
            },
        onTextLayout = { textLayoutResult = it },
        style = TextStyle(
            fontSize = 24.sp,
            fontFamily = hafsFont,
            lineHeight = 52.sp,
            textAlign = TextAlign.Justify,
            textDirection = TextDirection.Rtl
        )
    )
}
