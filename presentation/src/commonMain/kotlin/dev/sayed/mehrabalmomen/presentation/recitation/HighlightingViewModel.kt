package dev.sayed.mehrabalmomen.presentation.recitation

import androidx.lifecycle.ViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.tasmee3.RecitationUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf

open class HighlightingViewModel : ViewModel() {
    open val hasSeenDisclaimerFlow: Flow<Boolean> = flowOf(true)
    open fun markDisclaimerAsSeen() {}
    open val uiState: StateFlow<RecitationUiState> = MutableStateFlow(RecitationUiState())
    open fun setTargetSurah(surahId: Int, surahName: String, startAyah: Int = 1, endAyah: Int = 0) {}
    open fun startRecitationSession() {}
    open fun stopRecitationSession() {}
    open fun restartRecitationSession() {}
    open fun startModelDownload() {}
    open fun cancelDownload() {}
    open fun retryDownload() {}
    open fun dismissDownloadDialog() {}
    open fun dismissCompletionDialog() {}
    open fun selectWordForFeedback(globalWordIdx: Int, wordText: String) {}
    open fun dismissWordFeedback() {}
}
