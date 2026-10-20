package dev.sayed.mehrabalmomen.presentation.screen.recitation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.recitation.HighlightingViewModel
import dev.sayed.mehrabalmomen.presentation.recitation.WordStatus
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.components.BismillahSection
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.components.SurahAppBarSection
import org.koin.androidx.compose.koinViewModel

/**
 * Authentic Mushaf Tasmee' Screen with Hafs Calligraphy, Justified Layout, and 3-Color Protocol.
 */
@Composable
fun Tasmee3Screen(
    surahId: Int,
    surahName: String,
    navController: NavController,
    viewModel: HighlightingViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        if (granted) {
            viewModel.startRecitationSession()
        }
    }

    LaunchedEffect(surahId) {
        viewModel.setTargetSurah(surahId, surahName)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopRecitationSession()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Surah App Bar matching SurahAyatScreen
            SurahAppBarSection(
                surahName = "تسميع: $surahName",
                onBack = { navController.popBackStack() },
                onSearch = {},
                onTasmee3 = {}
            )

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = Theme.color.primary.primary,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "جاري فتح المصحف الشريف...",
                            style = Theme.textStyle.body.medium,
                            color = Theme.color.semantic.shadeTertiary
                        )
                    }
                }
            } else {
                // Mushaf Continuous Flow Text Area
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(
                        top = 8.dp,
                        bottom = 190.dp // Padding for the floating control box
                    )
                ) {
                    // Bismillah Header (except for Surah Al-Fatihah & Surah At-Tawbah)
                    if (surahId != 1 && surahId != 9) {
                        item {
                            BismillahSection(color = Theme.color.primary.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    item {
                        Tasmee3MushafSection(
                            verses = uiState.verses,
                            wordStatuses = uiState.wordStatuses,
                            activeCursor = uiState.activeWordCursor,
                            onWordClick = { globalWordIdx, wordText ->
                                viewModel.selectWordForFeedback(globalWordIdx, wordText)
                            }
                        )
                    }
                }
            }
        }

        // Tajweed / Pronunciation Feedback Bottom Sheet (when a word is tapped)
        uiState.selectedWordFeedback?.let { (wordText, mistakes) ->
            dev.sayed.mehrabalmomen.presentation.screen.recitation.components.TajweedErrorBottomSheet(
                wordText = wordText,
                mistakes = mistakes,
                onDismiss = viewModel::dismissWordFeedback
            )
        }

        // Floating Control & Feedback Box (Matching TilawahBox Design System)
        Tasmee3FloatingControlBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 12.dp, start = 16.dp, end = 16.dp),
            isLoading = uiState.isLoading,
            isRecording = uiState.isRecording,
            recognizedText = uiState.recognizedText,
            lastEvaluatedInfo = uiState.lastEvaluatedWordInfo,
            isTajweed = uiState.isTajweedEnabled,
            onToggleTajweed = viewModel::toggleTajweedMode,
            onToggleRecord = {
                if (uiState.isLoading) return@Tasmee3FloatingControlBar
                if (uiState.isRecording) {
                    viewModel.stopRecitationSession()
                } else {
                    if (hasPermission) {
                        viewModel.startRecitationSession()
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                }
            }
        )
    }
}

/**
 * Renders the entire Surah in an authentic, justified Mus'haf flow using Hafs calligraphy.
 */
@Composable
fun Tasmee3MushafSection(
    verses: List<dev.sayed.mehrabalmomen.data.recitation.data.QuranVerse>,
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
                    val globalIdx = verse.globalStartIndex + wordIdx
                    val status = wordStatuses[globalIdx] ?: WordStatus.UNSPOKEN

                    val isRevealed = status != WordStatus.UNSPOKEN

                    pushStringAnnotation(tag = "WORD_CLICK", annotation = "$globalIdx|$word")

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
                            append(word)
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
                    append("﴿${toArabicNumerals(verse.ayah)}﴾ ")
                }
            }
        }
    }

    androidx.compose.foundation.text.ClickableText(
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
            annotatedMushafText.getStringAnnotations(tag = "WORD_CLICK", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val parts = annotation.item.split("|")
                    if (parts.size >= 2) {
                        val gIdx = parts[0].toIntOrNull() ?: return@let
                        val wText = parts[1]
                        onWordClick(gIdx, wText)
                    }
                }
        }
    )
}

/**
 * Floating Tasmee' Control Card matching the app's design system (TilawahBox style).
 */
@Composable
fun Tasmee3FloatingControlBar(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
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
        targetValue = if (isRecording) 1.14f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = RoundedCornerShape(20.dp),
                shadow = Shadow(
                    radius = 16.dp,
                    offset = DpOffset(0.dp, 4.dp),
                    color = Color.Black.copy(alpha = 0.10f)
                )
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Theme.color.surfaces.surfaceLow)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Live Status Header & Record Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Recording / Listening state badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .scale(pulseScale)
                            .background(
                                color = when {
                                    isLoading -> Theme.color.primary.primary
                                    isRecording -> Color(0xFF2E7D32)
                                    else -> Color.Gray
                                },
                                shape = CircleShape
                            )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when {
                            isLoading -> "جاري تجهيز المصحف الشريف..."
                            isRecording -> "الميكروفون يستمع لتلاوتك..."
                            else -> "التسميع متوقف (اضغط للبدء)"
                        },
                        style = Theme.textStyle.label.medium,
                        color = when {
                            isLoading -> Theme.color.primary.primary
                            isRecording -> Color(0xFF2E7D32)
                            else -> Theme.color.semantic.shadeTertiary
                        },
                        fontWeight = FontWeight.Bold
                    )
                }

                // Main Action Button (Play / Stop / Loading)
                Box(
                    modifier = Modifier
                        .size(44.dp)
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
                        style = Theme.textStyle.label.small.copy(fontSize = 11.sp),
                        color = Theme.color.semantic.shadeTertiary
                    )
                }
                Switch(
                    checked = isTajweed,
                    onCheckedChange = onToggleTajweed,
                    modifier = Modifier.scale(0.80f),
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

/**
 * Converts Western digits (1, 2, 3...) to authentic Arabic numerals (١، ٢، ٣...).
 */
private fun toArabicNumerals(number: Int): String {
    val arabicDigits = charArrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    val str = number.toString()
    val builder = StringBuilder()
    for (char in str) {
        if (char in '0'..'9') {
            builder.append(arabicDigits[char - '0'])
        } else {
            builder.append(char)
        }
    }
    return builder.toString()
}
