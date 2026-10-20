package dev.sayed.mehrabalmomen.presentation.recitation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.data.recitation.audio.AudioPipeline
import dev.sayed.mehrabalmomen.data.recitation.data.QuranRepository
import dev.sayed.mehrabalmomen.data.recitation.data.QuranVerse
import dev.sayed.mehrabalmomen.data.recitation.engine.AsrTokenProcessor
import dev.sayed.mehrabalmomen.data.recitation.engine.SherpaEngine
import dev.sayed.mehrabalmomen.data.recitation.tracking.DictationSequencer
import dev.sayed.mehrabalmomen.data.recitation.tracking.MatchingStrictness
import dev.sayed.mehrabalmomen.data.recitation.tracking.TrackerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import dev.sayed.mehrabalmomen.data.recitation.tajweed.ReciterMistake

enum class WordStatus {
    UNSPOKEN, GREEN, RED, YELLOW, NEUTRAL
}

data class RecitationUiState(
    val isRecording: Boolean = false,
    val isLoading: Boolean = false,
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

/**
 * State Management ViewModel for AI Quran Recitation and Tajweed Verification.
 * Strictly adheres to speech-synchronous word revealing and omission tracking.
 */
class HighlightingViewModel(
    application: Application
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
    private val initDeferred = kotlinx.coroutines.CompletableDeferred<Unit>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            try {
                sherpaEngine.initialize()
                quranRepository.loadData()
            } finally {
                initDeferred.complete(Unit)
            }
        }
        observeAudioStream()
        observeAsrStream()
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

                // Epoch guard: reject stale results from a previous recording session
                if (result.streamEpoch != sherpaEngine.currentStreamEpoch) return@collect

                val processed = tokenProcessor.process(result)
                if (processed.isEmpty) return@collect

                val cleanText = processed.text.trim()
                // Ignore isolated single-character acoustic blips or silence noise
                if (cleanText.length < 2) return@collect

                val isNewSegment = isExpectingNewSegment || result.isFinal
                if (isExpectingNewSegment) isExpectingNewSegment = false

                // Dedup guard
                if (cleanText == lastProcessedText && !isNewSegment) return@collect
                lastProcessedText = cleanText

                _uiState.update { it.copy(recognizedText = cleanText) }

                // Feed character-level durations to sequencer on background worker
                sequencer.syncStream(cleanText, processed.charDurations, isNewSegment)
            }
        }
    }

    fun setTargetSurah(surahNum: Int, name: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, targetSurah = surahNum, surahName = name) }
            initDeferred.await()
            val verses = quranRepository.getVersesForSurah(surahNum)

            val allWords = verses.flatMap { it.phonemeWords }
            val boundaries = calculateBoundaries(allWords)
            val fullPhonemes = allWords.joinToString("")

            withContext(Dispatchers.Main) {
                wordStatuses.clear()
                wordMistakes.clear()
                lastProcessedText = ""
                tokenProcessor.reset()
                sherpaEngine.resetBuffer()

                sequencer.config = TrackerConfig(
                    enableEarlyMatching = false,
                    matchingStrictness = MatchingStrictness.normal
                )
                sequencer.setSurahReference(
                    phonemes = fullPhonemes,
                    boundaries = boundaries,
                    surahNum = surahNum,
                    tajweed = _uiState.value.isTajweedEnabled,
                    startWord = 0,
                    forceClear = true
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        targetSurah = surahNum,
                        surahName = name,
                        verses = verses,
                        wordStatuses = emptyMap(),
                        wordMistakes = emptyMap(),
                        selectedWordFeedback = null,
                        activeWordCursor = 0,
                        recognizedText = ""
                    )
                }
            }
        }
    }

    private fun calculateBoundaries(words: List<String>): List<Int> {
        val bounds = mutableListOf<Int>()
        var cursor = 0
        for (w in words) {
            bounds.add(cursor)
            cursor += w.replace(" ", "").length
        }
        bounds.add(cursor)
        return bounds
    }

    fun startRecitationSession() {
        if (_uiState.value.isLoading || _uiState.value.verses.isEmpty()) {
            return
        }
        wordStatuses.clear()
        lastProcessedText = ""
        tokenProcessor.reset()
        sherpaEngine.resetBuffer()
        isExpectingNewSegment = true

        _uiState.update {
            it.copy(
                isRecording = true,
                wordStatuses = emptyMap(),
                activeWordCursor = 0,
                recognizedText = ""
            )
        }

        val verses = _uiState.value.verses
        if (verses.isNotEmpty()) {
            val allWords = verses.flatMap { it.phonemeWords }
            val boundaries = calculateBoundaries(allWords)
            val fullPhonemes = allWords.joinToString("")
            sequencer.setSurahReference(
                phonemes = fullPhonemes,
                boundaries = boundaries,
                surahNum = _uiState.value.targetSurah,
                tajweed = _uiState.value.isTajweedEnabled,
                startWord = 0,
                forceClear = true
            )
        }

        audioPipeline.startStreaming(onError = {
            _uiState.update { state -> state.copy(isRecording = false) }
        })
    }

    fun stopRecitationSession() {
        _uiState.update { it.copy(isRecording = false) }
        audioPipeline.stopStreaming()
    }

    fun toggleTajweedMode(enabled: Boolean) {
        _uiState.update { it.copy(isTajweedEnabled = enabled) }
        sequencer.isTajweed = enabled
    }

    override fun onCleared() {
        super.onCleared()
        audioPipeline.stopStreaming()
        sherpaEngine.destroy()
    }
}
