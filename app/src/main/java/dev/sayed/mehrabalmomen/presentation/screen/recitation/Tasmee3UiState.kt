package dev.sayed.mehrabalmomen.presentation.screen.recitation

import dev.sayed.mehrabalmomen.data.recitation.data.QuranVerse
import dev.sayed.mehrabalmomen.data.recitation.tajweed.ReciterMistake

enum class WordStatus {
    UNSPOKEN, GREEN, RED, YELLOW, NEUTRAL
}

data class RecitationUiState(
    val isRecording: Boolean = false,
    val isLoading: Boolean = false,
    val isDownloadingModel: Boolean = false,
    val isProcessingModel: Boolean = false,
    val showDownloadDialog: Boolean = false,
    val downloadProgress: Float = 0f,
    val downloadFileStatus: String = "",
    val downloadError: String? = null,
    val targetSurah: Int = 1,
    val surahName: String = "",
    val verses: List<QuranVerse> = emptyList(),
    val wordStatuses: Map<Int, WordStatus> = emptyMap(),
    val wordMistakes: Map<Int, List<ReciterMistake>> = emptyMap(),
    val selectedWordFeedback: Pair<String, List<ReciterMistake>>? = null,
    val activeWordCursor: Int = 0,
    val recognizedText: String = "",
    val isTajweedEnabled: Boolean = true,
    val lastEvaluatedWordInfo: String = ""
)
