package dev.sayed.mehrabalmomen.presentation.recitation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.data.recitation.audio.AudioPipeline
import dev.sayed.mehrabalmomen.data.recitation.data.QuranRepository
import dev.sayed.mehrabalmomen.data.recitation.data.QuranVerse
import dev.sayed.mehrabalmomen.data.recitation.engine.AsrTokenProcessor
import dev.sayed.mehrabalmomen.data.recitation.engine.ModelDownloadState
import dev.sayed.mehrabalmomen.data.recitation.engine.RecitationModelDownloader
import dev.sayed.mehrabalmomen.data.recitation.engine.SherpaEngine
import dev.sayed.mehrabalmomen.data.recitation.tracking.DictationSequencer
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import dev.sayed.mehrabalmomen.data.recitation.tajweed.ReciterMistake
import dev.sayed.mehrabalmomen.presentation.screen.recitation.RecitationUiState
import dev.sayed.mehrabalmomen.presentation.screen.recitation.WordStatus
import dev.sayed.mehrabalmomen.presentation.screen.recitation.cleanWordText

/**
 * State Management ViewModel for AI Quran Recitation and Tajweed Verification.
 * Strictly adheres to speech-synchronous word revealing and omission tracking.
 */
class HighlightingViewModel(
    application: Application,
    private val modelDownloader: RecitationModelDownloader
) : AndroidViewModel(application) {

    private val audioPipeline = AudioPipeline()
    private val sherpaEngine = SherpaEngine(application)
    private val quranRepository = QuranRepository(application)
    private val tokenProcessor = AsrTokenProcessor()

    private val _uiState = MutableStateFlow(RecitationUiState())
    val uiState: StateFlow<RecitationUiState> = _uiState.asStateFlow()

    private val wordStatuses = mutableMapOf<Int, WordStatus>()
    private val wordMistakes = mutableMapOf<Int, List<ReciterMistake>>()

    private val sequencer = DictationSequencer { wordId, status, cleanAsr, mistakes ->
        wordStatuses[wordId] = status
        if (mistakes.isNotEmpty()) {
            wordMistakes[wordId] = mistakes
        }
        val statusSymbol = when (status) {
            WordStatus.GREEN  -> "🟢 صحيح"
            WordStatus.YELLOW -> "🟡 تنبيه تجويد (${mistakes.size})"
            WordStatus.RED    -> "🔴 خطأ / سقطت"
            else              -> ""
        }

        val targetWord = _uiState.value.verses
            .firstOrNull { wordId in it.globalStartIndex until (it.globalStartIndex + it.displayWords.size) }
            ?.let { verse ->
                verse.displayWords.getOrNull(wordId - verse.globalStartIndex)?.let { cleanWordText(it) }
            } ?: cleanAsr

        val infoText = if (targetWord.isNotEmpty()) {
            "« $targetWord »  $statusSymbol"
        } else {
            "كلمة #${wordId + 1}: $statusSymbol ${if (cleanAsr.isNotEmpty()) "(\"$cleanAsr\")" else ""}"
        }
        
        val totalWords = _uiState.value.verses.sumOf { it.phonemeWords.size }
        val isFinished = totalWords > 0 && (wordId + 1 >= totalWords)

        _uiState.update {
            it.copy(
                wordStatuses = wordStatuses.toMap(),
                wordMistakes = wordMistakes.toMap(),
                activeWordCursor = wordId + 1,
                lastEvaluatedWordInfo = infoText
            )
        }

        if (isFinished) {
            handleRecitationFinished()
        }
    }

    private fun handleRecitationFinished() {
        if (_uiState.value.isCompleted) return
        stopRecitationSession()
        val totalWords = _uiState.value.verses.sumOf { it.phonemeWords.size }
        val correct = wordStatuses.values.count { it == WordStatus.GREEN }
        val warning = wordStatuses.values.count { it == WordStatus.YELLOW }
        val error = wordStatuses.values.count { it == WordStatus.RED }

        val errorPercentage = if (totalWords > 0) (error * 100) / totalWords else 0
        val accuracyPercentage = (100 - errorPercentage).coerceIn(0, 100)

        val (ratingTitleRes, ratingMessageRes) = when {
            accuracyPercentage >= 95 -> Pair(
                dev.sayed.mehrabalmomen.R.string.recitation_rating_excellent_title,
                dev.sayed.mehrabalmomen.R.string.recitation_rating_excellent_msg
            )
            accuracyPercentage >= 85 -> Pair(
                dev.sayed.mehrabalmomen.R.string.recitation_rating_very_good_title,
                dev.sayed.mehrabalmomen.R.string.recitation_rating_very_good_msg
            )
            accuracyPercentage >= 70 -> Pair(
                dev.sayed.mehrabalmomen.R.string.recitation_rating_good_title,
                dev.sayed.mehrabalmomen.R.string.recitation_rating_good_msg
            )
            else -> Pair(
                dev.sayed.mehrabalmomen.R.string.recitation_rating_retry_title,
                dev.sayed.mehrabalmomen.R.string.recitation_rating_retry_msg
            )
        }

        val result = dev.sayed.mehrabalmomen.presentation.screen.recitation.RecitationCompletionResult(
            totalWords = totalWords,
            correctWords = correct,
            warningWords = warning,
            errorWords = error,
            accuracyPercentage = accuracyPercentage,
            ratingTitleRes = ratingTitleRes,
            ratingMessageRes = ratingMessageRes
        )

        _uiState.update {
            it.copy(
                isCompleted = true,
                showCompletionDialog = true,
                completionResult = result
            )
        }
    }

    fun selectWordForFeedback(globalWordId: Int, wordText: String) {
        val mistakes = wordMistakes[globalWordId]
        if (!mistakes.isNullOrEmpty()) {
            _uiState.update { it.copy(selectedWordFeedback = wordText to mistakes) }
        }
    }

    fun dismissWordFeedback() {
        _uiState.update { it.copy(selectedWordFeedback = null) }
    }

    private var lastProcessedText = ""
    private var isExpectingNewSegment = false
    private val initDeferred = CompletableDeferred<Unit>()

    init {
        observeDownloadState()
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            try {
                if (!modelDownloader.isModelReady()) {
                    _uiState.update { it.copy(showDownloadDialog = true, isLoading = false) }
                    quranRepository.loadData()
                    initDeferred.complete(Unit)
                    return@launch
                }
                sherpaEngine.initialize()
                quranRepository.loadData()
            } finally {
                _uiState.update { it.copy(isLoading = false) }
                initDeferred.complete(Unit)
            }
        }
        observeAudioStream()
        observeAsrStream()
    }

    private fun observeDownloadState() {
        viewModelScope.launch {
            modelDownloader.downloadState.collect { state ->
                when (state) {
                    is ModelDownloadState.Downloading -> {
                        _uiState.update {
                            it.copy(
                                isDownloadingModel = true,
                                isProcessingModel = false,
                                downloadProgress = state.progress,
                                downloadFileStatus = state.currentFile,
                                downloadError = null
                            )
                        }
                    }
                    is ModelDownloadState.Processing -> {
                        _uiState.update {
                            it.copy(
                                isDownloadingModel = false,
                                isProcessingModel = true,
                                downloadFileStatus = state.message,
                                downloadError = null
                            )
                        }
                    }
                    is ModelDownloadState.Error -> {
                        _uiState.update {
                            it.copy(
                                isDownloadingModel = false,
                                isProcessingModel = false,
                                downloadError = state.message
                            )
                        }
                    }
                    is ModelDownloadState.Ready -> {
                        _uiState.update {
                            it.copy(
                                isDownloadingModel = false,
                                isProcessingModel = false,
                                showDownloadDialog = false,
                                downloadError = null
                            )
                        }
                        sherpaEngine.initialize()
                        quranRepository.loadData()
                    }
                    else -> {}
                }
            }
        }
    }

    fun startModelDownload() {
        _uiState.update { it.copy(isDownloadingModel = true, downloadError = null) }
        try {
            dev.sayed.mehrabalmomen.data.recitation.engine.RecitationDownloadService.start(getApplication())
        } catch (e: Exception) {
            viewModelScope.launch(Dispatchers.IO) {
                val success = modelDownloader.downloadAndPrepareModels()
                if (success) {
                    _uiState.update { it.copy(isDownloadingModel = false, showDownloadDialog = false) }
                    sherpaEngine.initialize()
                    quranRepository.loadData()
                }
            }
        }
    }

    fun retryDownload() {
        startModelDownload()
    }

    fun dismissDownloadDialog() {
        cancelDownload()
    }

    fun cancelDownload() {
        dev.sayed.mehrabalmomen.data.recitation.engine.RecitationDownloadService.stop(getApplication())
        modelDownloader.resetState()
        _uiState.update {
            it.copy(
                isDownloadingModel = false,
                isProcessingModel = false,
                showDownloadDialog = false,
                downloadError = null
            )
        }
    }

    private fun observeAudioStream() {
        viewModelScope.launch(Dispatchers.Default) {
            audioPipeline.audioChunks.collect { chunk ->
                if (_uiState.value.isRecording) {
                    sherpaEngine.transcribe(chunk, isFinal = false)
                }
            }
        }
    }

    private fun observeAsrStream() {
        viewModelScope.launch(Dispatchers.Default) {
            sherpaEngine.transcriptionStream.collect { result ->
                if (!_uiState.value.isRecording) return@collect
                if (result.streamEpoch != sherpaEngine.currentStreamEpoch) return@collect

                val processed = tokenProcessor.process(result)
                if (processed.isEmpty) return@collect

                sequencer.syncStream(processed.text, processed.charDurations, isExpectingNewSegment)
                lastProcessedText = processed.text
                isExpectingNewSegment = false

                val fullNewText = result.text.trim()
                if (fullNewText.isNotEmpty()) {
                    _uiState.update { it.copy(recognizedText = fullNewText) }
                }
            }
        }
    }

    fun setTargetSurah(surahId: Int, name: String, startAyah: Int = 1, endAyah: Int = -1) {
        viewModelScope.launch(Dispatchers.IO) {
            initDeferred.await()

            val allVerses = quranRepository.getVersesForSurah(surahId)
            val effectiveStart = if (startAyah > 0) startAyah else 1
            val effectiveEnd = if (endAyah in effectiveStart..allVerses.size) endAyah else allVerses.size

            val filteredVerses = allVerses.filter { it.ayah in effectiveStart..effectiveEnd }

            var runningWordIndex = 0
            val verses = filteredVerses.map { verse ->
                val updated = verse.copy(globalStartIndex = runningWordIndex)
                runningWordIndex += verse.phonemeWords.size
                updated
            }

            val allPhonemes = verses.flatMap { it.phonemeWords }

            val joinedPhonemes = allPhonemes.joinToString("")
            val boundaries = mutableListOf(0)
            var cur = 0
            for (p in allPhonemes) {
                cur += p.length
                boundaries.add(cur)
            }

            sequencer.setSurahReference(
                phonemes = joinedPhonemes,
                boundaries = boundaries,
                surahNum = surahId,
                tajweed = false
            )

            wordStatuses.clear()
            wordMistakes.clear()

            _uiState.update {
                it.copy(
                    targetSurah = surahId,
                    surahName = name,
                    verses = verses,
                    wordStatuses = emptyMap(),
                    wordMistakes = emptyMap(),
                    selectedWordFeedback = null,
                    activeWordCursor = 0,
                    recognizedText = "",
                    lastEvaluatedWordInfo = "",
                    isCompleted = false,
                    showCompletionDialog = false,
                    completionResult = null,
                    isTajweedEnabled = false
                )
            }
        }
    }

    fun toggleTajweedMode(enabled: Boolean) {
        // Disabled per user request
        _uiState.update { it.copy(isTajweedEnabled = false) }
        sequencer.isTajweed = false
    }

    fun resetRecitationProgress() {
        wordStatuses.clear()
        wordMistakes.clear()

        val verses = _uiState.value.verses
        val allPhonemes = verses.flatMap { it.phonemeWords }
        val joinedPhonemes = allPhonemes.joinToString("")
        val boundaries = mutableListOf(0)
        var cur = 0
        for (p in allPhonemes) {
            cur += p.length
            boundaries.add(cur)
        }

        sequencer.setSurahReference(
            phonemes = joinedPhonemes,
            boundaries = boundaries,
            surahNum = _uiState.value.targetSurah,
            tajweed = false,
            startWord = 0,
            forceClear = true
        )

        _uiState.update {
            it.copy(
                wordStatuses = emptyMap(),
                wordMistakes = emptyMap(),
                selectedWordFeedback = null,
                activeWordCursor = 0,
                recognizedText = "",
                lastEvaluatedWordInfo = "",
                isCompleted = false,
                showCompletionDialog = false,
                completionResult = null
            )
        }
    }

    fun restartRecitationSession() {
        stopRecitationSession()
        resetRecitationProgress()
        startRecitationSession()
    }

    fun dismissCompletionDialog() {
        _uiState.update { it.copy(showCompletionDialog = false) }
    }

    fun startRecitationSession() {
        if (_uiState.value.isRecording) return

        // If previous recitation completed, reset progress so words are hidden again
        val totalWords = _uiState.value.verses.sumOf { it.phonemeWords.size }
        val shouldReset = _uiState.value.isCompleted || (totalWords > 0 && _uiState.value.activeWordCursor >= totalWords)

        if (shouldReset) {
            resetRecitationProgress()
        }

        viewModelScope.launch(Dispatchers.IO) {
            initDeferred.await()
            if (!modelDownloader.isModelReady()) {
                _uiState.update { it.copy(showDownloadDialog = true) }
                return@launch
            }
            sherpaEngine.initialize()
            tokenProcessor.reset()
            audioPipeline.startStreaming()
            sherpaEngine.resetBuffer()
            lastProcessedText = ""
            isExpectingNewSegment = true
            _uiState.update { it.copy(isRecording = true, showCompletionDialog = false) }
        }
    }

    fun stopRecitationSession() {
        if (!_uiState.value.isRecording) return
        _uiState.update { it.copy(isRecording = false) }
        viewModelScope.launch(Dispatchers.IO) {
            audioPipeline.stopStreaming()
            sherpaEngine.resetBuffer()
            tokenProcessor.reset()
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioPipeline.stopStreaming()
        sherpaEngine.destroy()
    }
}
