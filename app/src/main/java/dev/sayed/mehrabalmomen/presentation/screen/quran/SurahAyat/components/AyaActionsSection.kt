package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.components

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
    onTafseer: () -> Unit,
    onListen: () -> Unit
) {
    val context = LocalContext.current

    AnimatedVisibility(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .windowInsetsPadding(WindowInsets.navigationBars),
        visible = showActions,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        ) + fadeIn(tween(250)) + scaleIn(
            initialScale = 0.95f,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        ),
        exit = slideOutVertically(
            targetOffsetY = { it / 2 },
            animationSpec = tween(durationMillis = 200)
        ) + fadeOut(tween(150)) + scaleOut(
            targetScale = 0.95f,
            animationSpec = tween(durationMillis = 200)
        )
    ) {
        AyahActions(
            modifier = Modifier.padding(bottom = 24.dp),
            onActionClick = { action ->
                when (action) {
                    AyahAction.COPY -> onCopy()
                    AyahAction.BOOKMARK -> onBookmark()
//                    AyahAction.SEND -> {
//                        val cleanedText = cleanAyahTextForCopy(selectedAyaText)
//                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
//                            putExtra(Intent.EXTRA_TEXT, cleanedText)
//                            type = "text/plain"
//                        }
//                        context.startActivity(Intent.createChooser(shareIntent, "Share Aya"))
//                    }

                    AyahAction.TAFSEER -> onTafseer()
                    AyahAction.LISTEN -> onListen()
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