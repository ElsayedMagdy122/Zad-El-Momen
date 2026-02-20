package dev.sayed.mehrabalmomen.presentation.screen.SurahAyat.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.SurahAyat.SurahAyatUiState

@Composable
fun QuranTextSection(
    state: SurahAyatUiState,
    onAyaLongPressed: (Int, String) -> Unit,
    onClearSelection: () -> Unit,
    onAyaVisible: (Int) -> Unit,
    scrollState: LazyListState,
    onCalculatedPosition: (Float) -> Unit
) {
    val color = Theme.color.primary.primary
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }

    val surahText = remember(state.ayat, state.selectedAyaId) {
        buildAnnotatedString {
            state.ayat.forEach { aya ->
                val start = length
                val isSelected = state.selectedAyaId == aya.id
                val alpha = if (state.selectedAyaId == null || isSelected) 1f else 0.3f

                withStyle(SpanStyle(color = color.copy(alpha = alpha))) {
                    append(aya.text)
                }
                append(" ")
                val end = length
                addStringAnnotation(AYA_ID, aya.id.toString(), start, end)
                addStringAnnotation(AYA_TEXT, aya.text, start, end)
            }
        }
    }

    LaunchedEffect(scrollState, textLayoutResult) {
        androidx.compose.runtime.snapshotFlow {
            scrollState.firstVisibleItemScrollOffset
        }.collect { offset ->
            textLayoutResult?.let { layout ->
                val line = layout.getLineForVerticalPosition(offset.toFloat())
                val charOffset = layout.getLineStart(line)

                val visibleAyahId = surahText
                    .getStringAnnotations(AYA_ID, charOffset, charOffset)
                    .firstOrNull()
                    ?.item
                    ?.toInt()

                visibleAyahId?.let {
                    onAyaVisible(it)
                }
            }
        }
    }

    Text(
        text = surahText,
        onTextLayout = { layout ->
            textLayoutResult = layout
            state.targetAyahId?.let { targetId ->
                val index = state.ayat.indexOfFirst { it.id == targetId }
                if (index != -1) {
                    val annotations = surahText.getStringAnnotations(AYA_ID, 0, surahText.length)
                    val target = annotations.getOrNull(index)
                    target?.let {
                        val line = layout.getLineForOffset(it.start)
                        val yPosition = layout.getLineTop(line)
                        onCalculatedPosition(yPosition)
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onLongPress = { position ->
                        textLayoutResult?.let { layout ->
                            val offset = layout.getOffsetForPosition(position)
                            val id = surahText.getStringAnnotations(AYA_ID, offset, offset)
                                .firstOrNull()?.item?.toInt()
                            val text = surahText.getStringAnnotations(AYA_TEXT, offset, offset)
                                .firstOrNull()?.item
                            if (id != null && text != null) onAyaLongPressed(id, text)
                        }
                    },
                    onTap = { onClearSelection() }
                )
            },
        style = TextStyle(
            fontSize = 20.sp,
            fontFamily = FontFamily(Font(R.font.hafs)),
            lineHeight = 46.sp,
            textAlign = TextAlign.Justify,
            textDirection = TextDirection.Rtl,
            platformStyle = PlatformTextStyle(includeFontPadding = false)
        ),
    )
}

const val AYA_ID = "AYA_ID"
const val AYA_TEXT = "AYA_TEXT"