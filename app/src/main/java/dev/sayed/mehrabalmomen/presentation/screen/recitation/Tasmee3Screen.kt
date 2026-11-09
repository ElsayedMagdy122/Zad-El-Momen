package dev.sayed.mehrabalmomen.presentation.screen.recitation

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.components.QuranAppBar
import dev.sayed.mehrabalmomen.presentation.recitation.HighlightingViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.components.BismillahSection
import dev.sayed.mehrabalmomen.presentation.screen.recitation.components.RecitationDownloadDialog
import dev.sayed.mehrabalmomen.presentation.screen.recitation.components.TajweedErrorBottomSheet
import dev.sayed.mehrabalmomen.presentation.screen.recitation.components.Tasmee3FloatingControlBar
import dev.sayed.mehrabalmomen.presentation.screen.recitation.components.Tasmee3MushafSection
import org.koin.androidx.compose.koinViewModel

/**
 * Clean & Modularized Tasmee3 Screen.
 * Orchestrates UI State, Permission Requests, Navigation, and Design System Components.
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

    val isDownloadRequired = uiState.showDownloadDialog || uiState.isDownloadingModel || uiState.isProcessingModel || uiState.downloadError != null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Dedicated Tasmee3 Top App Bar with back navigation only
            QuranAppBar(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth()
                    .background(Theme.color.surfaces.surface),
                title = "تسميع سورة $surahName",
                onBackClick = { navController.popBackStack() },
                titleColor = Theme.color.primary.shadePrimary,
                actions = emptyList()
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = Theme.color.primary.primary,
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "يتطلب تنزيل نموذج التعرف الصوتي...",
                            style = Theme.textStyle.body.medium,
                            color = Theme.color.semantic.shadeTertiary
                        )
                    }
                }
            } else if (uiState.isLoading) {
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

        // Only show bottom feedback and floating control bar when download is NOT required
        if (!isDownloadRequired) {
            uiState.selectedWordFeedback?.let { (wordText, mistakes) ->
                TajweedErrorBottomSheet(
                    wordText = wordText,
                    mistakes = mistakes,
                    onDismiss = viewModel::dismissWordFeedback
                )
            }

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
}
