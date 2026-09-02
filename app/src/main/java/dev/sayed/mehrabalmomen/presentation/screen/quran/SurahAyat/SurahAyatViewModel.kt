package dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.entity.quran.bookmark.Bookmark
import dev.sayed.mehrabalmomen.domain.repository.quran.BookmarkRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioReadersRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.ReadingProgressRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.domain.repository.companion.CompanionRepository
import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerStatus
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.utils.toUiErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SurahAyatViewModel(
    private val quranRepository: QuranRepository,
    private val readingProgressRepository: ReadingProgressRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val settingsRepository: SettingsRepository,
    private val quranAudioRepository: QuranAudioRepository,
    private val readersRepository: QuranAudioReadersRepository,
    private val companionRepository: CompanionRepository,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<SurahAyatUiState, SurahAyatEffect>(
    SurahAyatUiState()
), SurahAyatInteractionListener, KoinComponent {

    private val audioPlayer: AudioPlayer by inject(named("quran"))

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
            readersRepository.observeLastReciter().collectLatest { lastReciter ->
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

                    if (isInitialLoad) {
                        loadTimingsAndPlay(
                            readerId = lastReciter.id,
                            ayahId = screenState.value.currentAudioAyahId,
                            autoPlay = false,
                            isAutomaticLoad = true
                        )
                    } else if (isNewReciter) {
                        audioPlayer.stop()
                        updateState { it.copy(showTilawahBox = true, showActions = false) }
                        loadTimingsAndPlay(
                            readerId = lastReciter.id,
                            ayahId = screenState.value.currentAudioAyahId,
                            autoPlay = false,
                            isAutomaticLoad = false
                        )
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
            loadTimingsAndPlay(
                readerId = readerId,
                ayahId = selectedAyah,
                autoPlay = false,
                isAutomaticLoad = false
            )
        }
    }

    private fun observeAudioPlayerState() {
        viewModelScope.launch {
            audioPlayer.playerState.collectLatest { audioState ->
                val isPlaying = audioState.status == AudioPlayerStatus.PLAYING
                val isBuffering = audioState.status == AudioPlayerStatus.BUFFERING

                updateState {
                    it.copy(
                        isAudioPlaying = isPlaying,
                        isAudioLoading = isBuffering
                    )
                }

                if (isPlaying) {
                    val currentPos = audioState.currentPositionMs
                    val state = screenState.value

                    val currentTiming = state.timings.find { it.verseNumber == state.currentAudioAyahId }
                    currentTiming?.let { timing ->
                        if (currentPos >= timing.endTimeMs - 250) {
                            if (state.repeatCount > 0 && state.currentRepeatIteration < state.repeatCount - 1) {
                                updateState { it.copy(currentRepeatIteration = it.currentRepeatIteration + 1) }
                                audioPlayer.seekTo(timing.startTimeMs)
                                return@collectLatest
                            } else {
                                if (!state.isContinuousReading) {
                                    audioPlayer.pause()
                                    audioPlayer.seekTo(timing.startTimeMs)
                                    updateState { it.copy(currentRepeatIteration = 0) }
                                    return@collectLatest
                                } else if (timing.verseNumber >= state.ayat.size) {
                                    audioPlayer.pause()
                                    updateState {
                                        it.copy(
                                            isContinuousReading = false,
                                            currentRepeatIteration = 0
                                        )
                                    }
                                    return@collectLatest
                                }
                            }
                        }
                    }

                    val detectedTiming = state.timings.find {
                        currentPos >= it.startTimeMs && currentPos < it.endTimeMs
                    }

                    detectedTiming?.let { timing ->
                        val detectedAyah = timing.verseNumber

                        if (state.currentAudioAyahId != detectedAyah) {
                            updateState {
                                it.copy(
                                    currentAudioAyahId = detectedAyah,
                                    selectedAyaId = detectedAyah,
                                    scrollToAyaId = detectedAyah,
                                    currentRepeatIteration = 0 
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun loadTimingsAndPlay(
        readerId: Int,
        ayahId: Int,
        autoPlay: Boolean = true,
        isAutomaticLoad: Boolean = false
    ) {
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
                        selectedAyaId = ayahId,
                        scrollToAyaId = ayahId,
                        currentAudioUrl = track?.audioUrl,
                        isAudioLoading = if (autoPlay && track != null) it.isAudioLoading else false
                    )
                }

                if (track != null && autoPlay) {
                    val targetTiming = timings.find { it.verseNumber == ayahId }
                    val startMs = targetTiming?.startTimeMs ?: 0L
                    withContext(Dispatchers.Main) {
                        audioPlayer.play(AudioSource.fromPath(track.audioUrl), startMs)
                    }
                }
            },
            onError = { throwable ->
                updateState {
                    it.copy(
                        isAudioLoading = false,
                        messageState = false,
                        selectedReaderId = if (isAutomaticLoad) null else it.selectedReaderId,
                        selectedReaderNameAr = if (isAutomaticLoad) null else it.selectedReaderNameAr,
                        selectedReaderNameEn = if (isAutomaticLoad) null else it.selectedReaderNameEn
                    )
                }

                if (!isAutomaticLoad) {
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
            audioPlayer.pause()
        } else {
            val currentAudioState = audioPlayer.playerState.value
            val currentSource = currentAudioState.currentSource
            val currentUrl = when (currentSource) {
                is AudioSource.RemoteUrl -> currentSource.url
                is AudioSource.LocalFile -> "file://${currentSource.path}"
                else -> null
            }
            val isSameReciter = currentUrl != null && currentUrl == state.currentAudioUrl

            if (state.timings.isEmpty() || !isSameReciter) {
                loadTimingsAndPlay(
                    readerId = state.selectedReaderId,
                    ayahId = state.currentAudioAyahId,
                    autoPlay = true,
                    isAutomaticLoad = false
                )
            } else {
                val currentTiming =
                    state.timings.find { it.verseNumber == state.currentAudioAyahId }
                currentTiming?.let { timing ->
                    if (currentAudioState.currentPositionMs >= timing.endTimeMs - 200) {
                        audioPlayer.seekTo(timing.startTimeMs)
                    }
                }
                audioPlayer.resume()
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
            audioPlayer.seekTo(timing.startTimeMs)
            audioPlayer.resume()
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
        audioPlayer.stop()
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
        audioPlayer.stop()
        sendEffect(
            SurahAyatEffect.NavigateToReciters(
                surahId = surahId,
                currentReaderId = screenState.value.selectedReaderId
            )
        )
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayer.stop()
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
                viewModelScope.launch {
                    companionRepository.updateQuranReadStatus(true)
                    companionRepository.updateLastInteraction(Clock.System.now().toEpochMilliseconds())
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
