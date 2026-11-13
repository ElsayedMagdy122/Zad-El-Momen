package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.design_system.text_style.Hafs
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.SurahAyatUiState
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

@Composable
fun QuranTextSection(
    state: SurahAyatUiState,
    listState: LazyListState,
    onAyaLongPressed: (Int, String) -> Unit,
    onClearSelection: () -> Unit,
    onVisibleAyahChanged: (Int) -> Unit = {}
) {
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val color = Theme.color.primary.primary

    val surahText = remember(state.ayat, state.selectedAyaId, state.currentAudioAyahId, color) {
        buildAnnotatedString {
            state.ayat.forEach { aya ->
                val isSelected = aya.id == state.selectedAyaId
                val isPlaying = aya.id == state.currentAudioAyahId
                val alpha = when {
                    isSelected -> 0.35f
                    isPlaying -> 0.20f
                    else -> 0f
                }

                withStyle(SpanStyle(color = color.copy(alpha = alpha))) {
                    pushStringAnnotation(tag = AYA_ID, annotation = aya.id.toString())
                    pushStringAnnotation(tag = AYA_TEXT, annotation = aya.text)
                    append(aya.text)
                    append(" ")
                    pop()
                    pop()
                }
            }
        }
    }

    LaunchedEffect(state.currentAudioAyahId, textLayoutResult) {
        val targetId = state.currentAudioAyahId
        val layout = textLayoutResult ?: return@LaunchedEffect

        val index = surahText.getStringAnnotations(AYA_ID, 0, surahText.length)
            .indexOfFirst { it.item.toIntOrNull() == targetId }

        if (index != -1) {
            val start = surahText.getStringAnnotations(AYA_ID, 0, surahText.length)[index].start
            val line = layout.getLineForOffset(start)
            val topOffset = layout.getLineTop(line).toInt()
            listState.animateScrollToItem(index = 0, scrollOffset = topOffset)
        }
    }

    LaunchedEffect(listState, textLayoutResult) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .map { offset ->
                val layout = textLayoutResult ?: return@map null
                val line = layout.getLineForVerticalPosition(offset.toFloat())
                val charOffset = layout.getLineStart(line)
                surahText.getStringAnnotations(AYA_ID, charOffset, charOffset)
                    .firstOrNull()?.item?.toIntOrNull()
            }
            .filterNotNull()
            .distinctUntilChanged()
            .debounce(150)
            .collect { ayahId ->
                onVisibleAyahChanged(ayahId)
            }
    }

    Text(
        text = surahText,
        onTextLayout = { textLayoutResult = it },
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
            fontSize = state.fontSize.sizeSp.sp,
            fontFamily = Hafs,
            lineHeight = 46.sp,
            textAlign = TextAlign.Justify,
            textDirection = TextDirection.Rtl
        ),
    )
}

const val AYA_ID = "AYA_ID"
const val AYA_TEXT = "AYA_TEXT"
