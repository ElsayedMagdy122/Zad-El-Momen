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
        val infoText = "كلمة #${wordId + 1}: $statusSymbol ${if (cleanAsr.isNotEmpty()) "(\"$cleanAsr\")" else ""}"
        _uiState.update {
            it.copy(
                wordStatuses = wordStatuses.toMap(),
                wordMistakes = wordMistakes.toMap(),
                activeWordCursor = wordId + 1,
                lastEvaluatedWordInfo = infoText
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

    fun setTargetSurah(surahId: Int, name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            initDeferred.await()

            val verses = quranRepository.getVersesForSurah(surahId)
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
                tajweed = _uiState.value.isTajweedEnabled
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
                    lastEvaluatedWordInfo = ""
                )
            }
        }
    }

    fun toggleTajweedMode(enabled: Boolean) {
        _uiState.update { it.copy(isTajweedEnabled = enabled) }
        sequencer.isTajweed = enabled
    }

    fun startRecitationSession() {
        if (_uiState.value.isRecording) return
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
            _uiState.update { it.copy(isRecording = true) }
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
