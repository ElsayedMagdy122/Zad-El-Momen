package dev.sayed.mehrabalmomen.presentation.screen.SurahAyat.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.sayed.mehrabalmomen.presentation.components.AyahAction
import dev.sayed.mehrabalmomen.presentation.components.AyahActions

@Composable
fun BoxScope.AyaActionsSection(
    showActions: Boolean,
    selectedAyaText: String,
    onCopy: () -> Unit,
    onBookmark: () -> Unit,
    onTafseer: () -> Unit
) {
    val context = LocalContext.current

    AnimatedVisibility(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .windowInsetsPadding(WindowInsets.navigationBars),
        visible = showActions,
        enter = fadeIn(tween(50)),
        exit = fadeOut(tween(50))
    ) {
        AyahActions(
            modifier = Modifier.padding(bottom = 24.dp),
            onActionClick = { action ->
                when (action) {
                    AyahAction.COPY -> onCopy()
                    AyahAction.BOOKMARK -> onBookmark()
                    AyahAction.SEND -> {
                        val cleanedText = cleanAyahTextForCopy(selectedAyaText)
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            putExtra(Intent.EXTRA_TEXT, cleanedText)
                            type = "text/plain"
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Aya"))
                    }

                    AyahAction.TAFSEER -> onTafseer()
                }
            }
        )
    }
}
 fun cleanAyahTextForCopy(text: String): String {
    var cleaned = text.trim()

//    if (cleaned.isNotEmpty() && cleaned.last().code in 0xFBD0..0xFDFF || cleaned.last().code in 0xFE70..0xFEFF) {
//        cleaned = cleaned.dropLast(1).trimEnd()
//    }

    cleaned = cleaned.filter {
        it.isWhitespace() || it in '\u0600'..'\u06FF' || it in '\u0750'..'\u077F' || it in '\u08A0'..'\u08FF'
    }

    return cleaned
}