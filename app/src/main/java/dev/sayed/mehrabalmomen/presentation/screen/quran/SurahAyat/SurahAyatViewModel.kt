package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.data.settings.local.RecitationPreferences
import dev.sayed.mehrabalmomen.domain.entity.quran.bookmark.Bookmark
import dev.sayed.mehrabalmomen.domain.repository.quran.BookmarkRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.ReadingProgressRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.audio_utils.AudioPlayerManager
import dev.sayed.mehrabalmomen.presentation.screen.quran.audio_utils.AudioPlayerState
import dev.sayed.mehrabalmomen.presentation.utils.toUiErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SurahAyatViewModel(
    private val quranRepository: QuranRepository,
    private val readingProgressRepository: ReadingProgressRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val settingsRepository: SettingsRepository,
    private val quranAudioRepository: QuranAudioRepository,
    private val audioPlayerManager: AudioPlayerManager,
    private val recitationPreferences: RecitationPreferences,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<SurahAyatUiState, SurahAyatEffect>(
    SurahAyatUiState()
), SurahAyatInteractionListener {

    private val surahId: Int = checkNotNull(savedStateHandle["surahId"])
    private val arabicName: String = checkNotNull(savedStateHandle["arabicName"])
    private val englishName: String = checkNotNull(savedStateHandle["englishName"])
    private val targetAyahId: Int? = savedStateHandle["targetAyahId"]

    init {
        loadSurahAyat()
        observeFontSize()
        observeAudioPlayerState()
        loadLastReciter()
    }

    private fun loadLastReciter() {
        viewModelScope.launch {
            recitationPreferences.lastReciter.collectLatest { lastReciter ->
                if (lastReciter != null) {
                    val currentId = screenState.value.selectedReaderId
                    val isInitialLoad = currentId == null
                    val isNewReciter = !isInitialLoad && currentId != lastReciter.id

                    updateState {
                        it.copy(
                            selectedReaderId = lastReciter.id,
                            selectedReaderNameAr = lastReciter.nameAr,
                            selectedReaderNameEn = lastReciter.nameEn
                        )
                    }

                    if (isNewReciter) {
                        // User selected a new reciter from list/search
                        updateState { it.copy(showTilawahBox = true, showActions = false) }
                        loadTimingsAndPlay(lastReciter.id, screenState.value.currentAudioAyahId, autoPlay = false)
                    }
                }
            }
        }
    }

    fun onListenToAyah() {
        val selectedAyah = screenState.value.selectedAyaId ?: 1
        val readerId = screenState.value.selectedReaderId

        updateState {
            it.copy(
                showActions = false,
                currentAudioAyahId = selectedAyah,
                showTilawahBox = true
            )
        }

        if (readerId != null) {
            loadTimingsAndPlay(readerId, selectedAyah, autoPlay = false)
        }
    }

    private fun observeAudioPlayerState() {
        viewModelScope.launch {
            audioPlayerManager.playerState.collectLatest { audioState ->
                val isPlaying =
                    audioState.playbackState == AudioPlayerState.AudioPlaybackState.PLAYING
                val isBuffering =
                    audioState.playbackState == AudioPlayerState.AudioPlaybackState.BUFFERING

                updateState {
                    it.copy(
                        isAudioPlaying = isPlaying,
                        isAudioLoading = isBuffering
                    )
                }

                if (isPlaying) {
                    val currentPos = audioState.currentPositionMs
                    val state = screenState.value
                    val currentTiming = state.timings.find {
                        currentPos >= it.startTimeMs && currentPos <= it.endTimeMs
                    }

                    currentTiming?.let { timing ->
                        val currentAyah = timing.verseNumber
                        
                        // Update UI to current ayah being played
                        if (state.currentAudioAyahId != currentAyah) {
                             updateState { it.copy(currentAudioAyahId = currentAyah) }
                        }

                        // Check for repetition at the end of the ayah
                        if (currentPos >= timing.endTimeMs - 250) {
                            if (state.repeatCount > 0 && state.currentRepeatIteration < state.repeatCount - 1) {
                                // Repeat the current ayah
                                updateState { it.copy(currentRepeatIteration = it.currentRepeatIteration + 1) }
                                audioPlayerManager.seekTo(timing.startTimeMs)
                                return@collectLatest
                            } else {
                                // Repeats finished for this ayah
                                updateState { it.copy(currentRepeatIteration = 0) }
                                
                                if (!state.isContinuousReading) {
                                    // Stop if not continuous
                                    audioPlayerManager.pause()
                                    audioPlayerManager.seekTo(timing.startTimeMs)
                                } else if (currentAyah >= state.ayat.size) {
                                    // Stop if end of surah
                                    audioPlayerManager.pause()
                                    updateState { it.copy(isContinuousReading = false) }
                                }
                                // If continuous, ExoPlayer will naturally flow or we rely on next timing match
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadTimingsAndPlay(readerId: Int, ayahId: Int, autoPlay: Boolean = true) {
        tryToCall(
            onStart = {
                if (autoPlay) {
                    updateState { it.copy(isAudioLoading = true) }
                }
            },
            block = {
                val timings = quranAudioRepository.getVerseTimings(readerId, surahId)
                val track = quranAudioRepository.getTrack(readerId, surahId)
                Pair(timings, track)
            },
            onSuccess = { (timings, track) ->
                updateState {
                    it.copy(
                        timings = timings,
                        currentRepeatIteration = 0,
                        currentAudioAyahId = ayahId,
                        isAudioLoading = false
                    )
                }

                if (track != null && autoPlay) {
                    val targetTiming = timings.find { it.verseNumber == ayahId }
                    val startMs = targetTiming?.startTimeMs ?: 0L
                    withContext(Dispatchers.Main) {
                        audioPlayerManager.play(track.audioUrl, startMs)
                    }
                } else {
                    updateState { it.copy(isAudioLoading = false) }
                }
            },
            onError = { throwable ->
                updateState { it.copy(isAudioLoading = false, messageState = false) }
                sendEffect(
                    SurahAyatEffect.ShowToast(
                        ToastDetails(
                            title = R.string.error,
                            message = throwable.toUiErrorMessage(),
                            icon = R.drawable.ic_close_circle
                        )
                    )
                )
            }
        )
    }

    fun onPlayPauseClick() {
        val state = screenState.value
        if (state.selectedReaderId == null) {
            updateState {
                it.copy(messageState = false)
            }
            sendEffect(
                SurahAyatEffect.ShowToast(
                    ToastDetails(
                        title = R.string.error,
                        message = R.string.select_reciter_first,
                        icon = R.drawable.ic_close_circle
                    )
                )
            )
            return
        }

        if (state.isAudioPlaying) {
            audioPlayerManager.pause()
        } else {
            val currentPlayerUrl = audioPlayerManager.playerState.value.currentUrl
            if (state.timings.isEmpty() || currentPlayerUrl == null) {
                loadTimingsAndPlay(state.selectedReaderId, state.currentAudioAyahId, autoPlay = true)
            } else {
                val currentTiming =
                    state.timings.find { it.verseNumber == state.currentAudioAyahId }
                currentTiming?.let { timing ->
                    if (audioPlayerManager.playerState.value.currentPositionMs >= timing.endTimeMs - 200) {
                        audioPlayerManager.seekTo(timing.startTimeMs)
                    }
                }
                audioPlayerManager.resume()
            }
        }
    }

    fun onForwardClick() {
        val nextAyah = screenState.value.currentAudioAyahId + 1
        if (nextAyah <= screenState.value.ayat.size) {
            seekToAyah(nextAyah)
        }
    }

    fun onBackwardClick() {
        val prevAyah = screenState.value.currentAudioAyahId - 1
        if (prevAyah >= 1) {
            seekToAyah(prevAyah)
        }
    }

    private fun seekToAyah(ayahNumber: Int) {
        val timing = screenState.value.timings.find { it.verseNumber == ayahNumber }
        if (timing != null) {
            audioPlayerManager.seekTo(timing.startTimeMs)
            audioPlayerManager.resume()
            updateState {
                it.copy(
                    currentAudioAyahId = ayahNumber,
                    scrollToAyaId = ayahNumber,
                    targetAyahId = ayahNumber,
                    selectedAyaId = ayahNumber
                )
            }
        }
    }

    fun onToggleRepeat() {
        updateState {
            val nextRepeat = if (it.repeatCount == 0) 3 else 0
            it.copy(
                repeatCount = nextRepeat,
                currentRepeatIteration = 0,
                isContinuousReading = if (nextRepeat > 0) false else it.isContinuousReading
            )
        }
    }

    fun onToggleContinuous() {
        updateState {
            val nextContinuous = !it.isContinuousReading
            it.copy(
                isContinuousReading = nextContinuous,
                repeatCount = if (nextContinuous) 0 else it.repeatCount
            )
        }
    }

    fun onCloseTilawahBox() {
        audioPlayerManager.stop()
        updateState {
            it.copy(
                showTilawahBox = false,
                selectedAyaId = null,
                selectedAyaText = "",
                showActions = false,
                timings = emptyList()
            )
        }
    }

    fun onClickReciters() {
        sendEffect(
            SurahAyatEffect.NavigateToReciters(
                surahId = surahId,
                currentReaderId = screenState.value.selectedReaderId
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerManager.stop()
    }

    fun onAyahVisible(ayahId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            readingProgressRepository.save(surahId = surahId, ayahId = ayahId)
        }
    }

    private fun observeFontSize() {
        viewModelScope.launch {
            settingsRepository.observeQuranFontSize().collect { size ->
                val font =
                    QuranFontSize.entries.firstOrNull { it.sizeSp == size } ?: QuranFontSize.MEDIUM
                updateState { it.copy(fontSize = font) }
            }
        }
    }

    private fun loadSurahAyat() {
        tryToCall(
            onStart = { updateState { state -> state.copy(isLoading = true) } },
            block = { quranRepository.getAyahs(surahId) },
            onSuccess = { ayat ->
                delay(200)
                updateState {
                    it.copy(
                        ayat = ayat.map { AyaUi(it.ayahNumber, it.text) },
                        isLoading = false,
                        arabicName = arabicName,
                        englishName = englishName,
                        selectedAyaId = targetAyahId,
                        scrollToAyaId = targetAyahId,
                        targetAyahId = targetAyahId,
                    )
                }
            },
            onError = { throwable ->
                updateState { it.copy(isLoading = false, messageState = false) }
                sendEffect(
                    SurahAyatEffect.ShowToast(
                        ToastDetails(
                            title = R.string.error,
                            message = throwable.toUiErrorMessage(),
                            icon = R.drawable.ic_close_circle
                        )
                    )
                )
            }
        )
    }

    fun onScrolledToTarget() {
        updateState { it.copy(targetAyahId = null, scrollToAyaId = null) }
    }

    override fun onAyaLongPressed(id: Int, text: String) {
        updateState {
            it.copy(
                selectedAyaId = id,
                selectedAyaText = text,
                showActions = true
            )
        }
    }

    override fun onClearSelection() {
        if (screenState.value.showTilawahBox) return
        updateState {
            it.copy(
                selectedAyaId = null,
                selectedAyaText = "",
                showActions = false
            )
        }
    }

    override fun onCopyAya() {
        val text = screenState.value.selectedAyaText
        if (text.isBlank()) return
        sendEffect(SurahAyatEffect.CopyAya(text))
        onClearSelection()
        updateState {
            it.copy(messageState = true)
        }
        sendEffect(
            SurahAyatEffect.ShowToast(
                ToastDetails(
                    title = R.string.success,
                    message = R.string.copied_message_successfully,
                    icon = R.drawable.ic_check_circle
                )
            )
        )
    }

    override fun onBookmarkAya() {
        val ayahId = screenState.value.selectedAyaId ?: return
        val ayahText = screenState.value.selectedAyaText
        if (ayahText.isBlank()) return

        tryToCall(
            onStart = { updateState { it.copy(showActions = false) } },
            block = {
                bookmarkRepository.addBookmark(
                    Bookmark(
                        surahId = surahId,
                        ayahId = ayahId,
                        arabicName = arabicName,
                        englishName = englishName,
                        text = ayahText
                    )
                )
            },
            onSuccess = {
                onClearSelection()
                updateState {
                    it.copy(messageState = true)
                }
                sendEffect(
                    SurahAyatEffect.ShowToast(
                        ToastDetails(
                            title = R.string.success,
                            message = R.string.ayah_bookmarked_message_successfully,
                            icon = R.drawable.ic_check_circle
                        )
                    )
                )
            },
            onError = { throwable ->
                updateState {
                    it.copy(messageState = false)
                }
                sendEffect(
                    SurahAyatEffect.ShowToast(
                        ToastDetails(
                            title = R.string.error,
                            message = throwable.toUiErrorMessage(),
                            icon = R.drawable.ic_close_circle
                        )
                    )
                )
            }
        )
    }

    override fun onTafseer() {
        val ayahId = screenState.value.selectedAyaId ?: return
        val ayaText = screenState.value.selectedAyaText

        tryToCall(
            onStart = {
                updateState {
                    it.copy(
                        showTafseerSheet = true,
                        tafseerUi = null,
                        showActions = false
                    )
                }
            },
            block = { quranRepository.getAyahTafseer(surahId, ayahId) },
            onSuccess = { tafseer ->
                updateState {
                    it.copy(
                        tafseerUi = TafseerUi(
                            ayahUi = AyaUi(id = ayahId, text = ayaText),
                            text = tafseer
                        )
                    )
                }
            },
            onError = { throwable ->
                updateState { it.copy(showTafseerSheet = false) }
                sendEffect(
                    SurahAyatEffect.ShowToast(
                        ToastDetails(
                            title = R.string.error,
                            message = throwable.toUiErrorMessage(),
                            icon = R.drawable.ic_close_circle
                        )
                    )
                )
            }
        )
    }

    val isAudioLoading: Boolean
        get() = screenState.value.isAudioLoading

    fun onDismissTafseerSheet() {
        updateState {
            it.copy(
                showTafseerSheet = false,
                tafseerUi = null,
                selectedAyaId = null,
                selectedAyaText = "",
                showActions = false
            )
        }
    }

    override fun onClickBack() {
        sendEffect(SurahAyatEffect.NavigateToBack)
    }

    override fun onClickSearch() {
        sendEffect(
            SurahAyatEffect.NavigateToSearch(
                surahId = surahId,
                arabicName = screenState.value.arabicName,
                englishName = screenState.value.englishName
            )
        )
    }
}