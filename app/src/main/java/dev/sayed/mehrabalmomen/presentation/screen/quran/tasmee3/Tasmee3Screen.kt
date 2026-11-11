package dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import dev.sayed.mehrabalmomen.presentation.components.LoadingContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.compose.ui.res.painterResource
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.components.AppBarAction
import dev.sayed.mehrabalmomen.presentation.components.QuranAppBar
import dev.sayed.mehrabalmomen.presentation.recitation.HighlightingViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.components.BismillahSection
import dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.components.RecitationCompletionBottomSheet
import dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.components.RecitationDisclaimerDialog
import dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.components.RecitationDownloadDialog
import dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.components.TajweedErrorBottomSheet
import dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.components.Tasmee3FloatingControlBar
import dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.components.Tasmee3MushafSection
import org.koin.androidx.compose.koinViewModel

/**
 * Clean & Modularized Tasmee3 Screen.
 * Orchestrates UI State, Permission Requests, Navigation, and Design System Components.
 */
@Composable
fun Tasmee3Screen(
    surahId: Int,
    surahName: String,
    startAyah: Int = 1,
    endAyah: Int = -1,
    navController: NavController,
    viewModel: HighlightingViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val appLocale = LocalAppLocale.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()
    var showDisclaimerDialog by remember { mutableStateOf(false) }

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

    LaunchedEffect(surahId, startAyah, endAyah) {
        viewModel.setTargetSurah(surahId, surahName, startAyah, endAyah)
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.stopRecitationSession()
        }
    }

    val isDownloadRequired = uiState.showDownloadDialog || uiState.isDownloadingModel || uiState.isProcessingModel || uiState.downloadError != null
    val isContentLoading = uiState.isLoading || uiState.verses.isEmpty()

    // Safe BackHandler: if downloading in background, allow user to navigate back safely while keeping download alive
    BackHandler {
        if (uiState.isDownloadingModel || uiState.isProcessingModel) {
            navController.popBackStack()
        } else {
            viewModel.dismissDownloadDialog()
            navController.popBackStack()
        }
    }

    val isCustomRange = startAyah > 1 || endAyah != -1
    val endDisplay = if (endAyah > 0) endAyah else uiState.verses.lastOrNull()?.ayah ?: ""
    val titleText = if (isCustomRange && endDisplay.toString().isNotEmpty()) {
        localizedString(
            R.string.tasmee3_title_range,
            surahName,
            startAyah.toString().toLocalizedDigits(appLocale),
            endDisplay.toString().toLocalizedDigits(appLocale)
        )
    } else {
        localizedString(R.string.tasmee3_title_full, surahName)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Dedicated Tasmee3 Top App Bar with back navigation and warning action
            QuranAppBar(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .background(Theme.color.surfaces.surface),
                title = titleText,
                onBackClick = {
                    if (uiState.isDownloadingModel || uiState.isProcessingModel) {
                        navController.popBackStack()
                    } else {
                        viewModel.dismissDownloadDialog()
                        navController.popBackStack()
                    }
                },
                titleColor = Theme.color.primary.shadePrimary,
                actions = listOf(
                    AppBarAction(
                        icon = painterResource(R.drawable.ic_warning),
                        onClick = { showDisclaimerDialog = true }
                    )
                )
            )

            // Show Mandatory Download Dialog using Design System PrimaryDialog
            if (isDownloadRequired) {
                RecitationDownloadDialog(
                    isDownloading = uiState.isDownloadingModel,
                    isProcessing = uiState.isProcessingModel,
                    progress = uiState.downloadProgress,
                    statusText = uiState.downloadFileStatus,
                    errorText = uiState.downloadError,
                    onConfirmDownload = { viewModel.startModelDownload() },
                    onContinueInBackground = {
                        navController.popBackStack()
                    },
                    onCancel = {
                        viewModel.cancelDownload()
                        navController.popBackStack()
                    },
                    onRetry = { viewModel.retryDownload() }
                )

                // Hide screen content behind clean loading container while model download is pending
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingContainer()
                }
            } else if (isContentLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingContainer()
                }
            } else {
                // Mushaf Continuous Flow Text Area - Chunked for 60fps performance in large Surahs
                val verseChunks = remember(uiState.verses) {
                    if (uiState.verses.size <= 8) listOf(uiState.verses)
                    else uiState.verses.chunked(6)
                }

                val hasBismillah = startAyah == 1 && surahId != 1 && surahId != 9
                val activeChunkIndex = remember(uiState.activeWordCursor, verseChunks) {
                    verseChunks.indexOfFirst { chunk ->
                        val start = chunk.firstOrNull()?.globalStartIndex ?: 0
                        val end = chunk.lastOrNull()?.let { it.globalStartIndex + it.phonemeWords.size } ?: 0
                        uiState.activeWordCursor in start until end
                    }
                }

                // Smoothly animated auto-scroll to keep the reciting cursor in comfortable view
                LaunchedEffect(activeChunkIndex, uiState.isRecording) {
                    if (uiState.isRecording && activeChunkIndex >= 0) {
                        val targetItemIndex = if (hasBismillah) activeChunkIndex + 1 else activeChunkIndex
                        scrollState.animateScrollToItem(targetItemIndex)
                    }
                }

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
                    // Bismillah Header (except for Surah Al-Fatihah & Surah At-Tawbah, and only when starting from Ayah 1)
                    if (startAyah == 1 && surahId != 1 && surahId != 9) {
                        item {
                            BismillahSection(color = Theme.color.primary.primary)
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    items(
                        items = verseChunks,
                        key = { chunk -> chunk.firstOrNull()?.ayah ?: 0 }
                    ) { chunk ->
                        Tasmee3MushafSection(
                            verses = chunk,
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

        // Only show word feedback when download is NOT required
        if (!isDownloadRequired) {
            uiState.selectedWordFeedback?.let { (wordText, mistakes) ->
                TajweedErrorBottomSheet(
                    wordText = wordText,
                    mistakes = mistakes,
                    onDismiss = viewModel::dismissWordFeedback
                )
            }
        }

        // Floating Control Bar smoothly animates in only when the Mushaf is ready and loaded
        AnimatedVisibility(
            visible = !isContentLoading && !isDownloadRequired && uiState.verses.isNotEmpty(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(bottom = 16.dp)
                .padding(horizontal = 16.dp),
            enter = slideInVertically(
                initialOffsetY = { it },
                animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing)
            ) + fadeIn(animationSpec = tween(400)),
            exit = slideOutVertically(
                targetOffsetY = { it },
                animationSpec = tween(durationMillis = 250)
            ) + fadeOut(animationSpec = tween(250))
        ) {
            Tasmee3FloatingControlBar(
                isLoading = uiState.isLoading,
                isRecording = uiState.isRecording,
                recognizedText = uiState.recognizedText,
                lastEvaluatedInfo = uiState.lastEvaluatedWordInfo,
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

        if (uiState.showCompletionDialog && uiState.completionResult != null) {
            RecitationCompletionBottomSheet(
                result = uiState.completionResult!!,
                surahName = surahName,
                onRestart = {
                    if (hasPermission) {
                        viewModel.restartRecitationSession()
                    } else {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onDismiss = { viewModel.dismissCompletionDialog() }
            )
        }

        if (showDisclaimerDialog) {
            RecitationDisclaimerDialog(
                onDismiss = { showDisclaimerDialog = false }
            )
        }
    }
}
