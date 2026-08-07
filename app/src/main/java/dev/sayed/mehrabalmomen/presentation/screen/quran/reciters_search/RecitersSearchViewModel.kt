package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters_search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.sayed.mehrabalmomen.data.settings.local.RecitationPreferences
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioReader
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioReadersRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.quran.audio_utils.AudioPlayerManager
import dev.sayed.mehrabalmomen.presentation.screen.quran.audio_utils.AudioPlayerState
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.DownloadState
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.PlayState
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.toUiState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class RecitersSearchViewModel(
    private val readersRepository: QuranAudioReadersRepository,
    private val quranAudioRepository: QuranAudioRepository,
    private val audioPlayerManager: AudioPlayerManager,
    private val recitationPreferences: RecitationPreferences,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<RecitersSearchUiState, RecitersSearchEffect>(RecitersSearchUiState()) {

    private val surahId: Int = savedStateHandle.toRoute<Route.RecitersSearchScreen>().surahId
    private val currentReaderId: Int? =
        savedStateHandle.toRoute<Route.RecitersSearchScreen>().currentReaderId
    private val _searchQuery = MutableStateFlow("")
    private var allReaders: List<QuranAudioReader> = emptyList()

    init {
        updateState { it.copy(surahId = surahId) }
        loadInitialData()
        observeSearchQuery()
        observeAudioState()
        observeDownloadedReciters()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                allReaders = readersRepository.getReaders()
            } catch (e: Exception) {
                allReaders = readersRepository.getDownloadedRecitersOnce()
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        updateState { it.copy(searchQuery = query) }
        _searchQuery.value = query
    }

    fun setLocale(isArabic: Boolean) {
        updateState { it.copy(isArabic = isArabic) }
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(100L)
                .distinctUntilChanged()
                .collectLatest { query ->
                    performSearch(query)
                }
        }
    }

    private suspend fun performSearch(query: String) {
        if (query.isBlank()) {
            updateState { it.copy(results = emptyList(), isLoading = false) }
            return
        }

        updateState { it.copy(isLoading = true) }

        val filtered = allReaders.filter { reader ->
            reader.nameAr.contains(query, ignoreCase = true) ||
                    reader.nameEn.contains(query, ignoreCase = true)
        }.map { reader ->
            val isDownloaded = if (readersRepository.isReciterDownloaded(reader.id, surahId)) {
                DownloadState.DOWNLOADED
            } else {
                DownloadState.NOT_DOWNLOADED
            }
            reader.toUiState(
                isArabic = screenState.value.isArabic,
                isDownloaded = isDownloaded,
                isSelected = reader.id == currentReaderId
            )
        }

        updateState { it.copy(results = filtered, isLoading = false) }
    }

    fun onBackClick() {
        sendEffect(RecitersSearchEffect.NavigateBack)
    }

    fun onReciterClick(readerId: Int) {
        val selectedReciter = allReaders.find { it.id == readerId } ?: return
        viewModelScope.launch {
            recitationPreferences.saveLastReciter(
                id = selectedReciter.id,
                nameAr = selectedReciter.nameAr,
                nameEn = selectedReciter.nameEn,
                baseAudioUrl = selectedReciter.baseAudioUrl,
                rewayaName = selectedReciter.rewaya?.nameAr ?: ""
            )
            updateState { state ->
                state.copy(
                    results = state.results.map {
                        it.copy(isSelected = it.id == readerId)
                    }
                )
            }
            sendEffect(RecitersSearchEffect.NavigateBack)
        }
    }

    fun onDownloadClick(reciterId: Int) {
        val domainReciter = allReaders.find { it.id == reciterId } ?: return
        readersRepository.downloadReciter(domainReciter, surahId)

        updateState { state ->
            state.copy(
                results = state.results.map {
                    if (it.id == reciterId) it.copy(downloadState = DownloadState.DOWNLOADING) else it
                }
            )
        }
        observeDownloadProgress(reciterId)
    }

    private fun observeDownloadProgress(reciterId: Int) {
        viewModelScope.launch {
            readersRepository.getDownloadWorkInfo(reciterId, surahId).collectLatest { workInfos ->
                val workInfo = workInfos.firstOrNull() ?: return@collectLatest
                val progress = workInfo.progress.getInt("progress", 0)
                updateState { state ->
                    state.copy(
                        results = state.results.map {
                            if (it.id == reciterId) it.copy(downloadProgress = progress) else it
                        }
                    )
                }
            }
        }
    }

    private fun observeDownloadedReciters() {
        viewModelScope.launch {
            readersRepository.getDownloadedReciters().collectLatest { downloaded ->
                updateState { state ->
                    val updatedResults = state.results.map { reciter ->
                        val isDownloaded = downloaded.any { it.id == reciter.id }
                        if (isDownloaded) {
                            reciter.copy(downloadState = DownloadState.DOWNLOADED)
                        } else {
                            reciter
                        }
                    }
                    state.copy(results = updatedResults)
                }
            }
        }
    }

    private fun observeAudioState() {
        viewModelScope.launch {
            audioPlayerManager.playerState.collect { audioState ->
                updateState { state ->
                    val updatedResults = state.results.map { reciter ->
                        val isLocalUrl =
                            audioState.currentUrl?.contains("audio/reciters/${reciter.id}/") == true
                        val isRemoteUrl =
                            audioState.currentUrl?.startsWith(reciter.baseAudioUrl.trimEnd('/')) == true
                        val isCurrentReciter = isLocalUrl || isRemoteUrl

                        if (isCurrentReciter) {
                            val newPlayState = when (audioState.playbackState) {
                                AudioPlayerState.AudioPlaybackState.BUFFERING -> PlayState.LOADING
                                AudioPlayerState.AudioPlaybackState.PLAYING -> PlayState.PLAY
                                else -> PlayState.RESUME
                            }
                            reciter.copy(playState = newPlayState)
                        } else {
                            reciter.copy(playState = PlayState.RESUME)
                        }
                    }
                    state.copy(results = updatedResults)
                }
            }
        }
    }

    fun onPlayClick(reciterId: Int) {
        val targetReciter = screenState.value.results.find { it.id == reciterId } ?: return
        val currentAudioState = audioPlayerManager.playerState.value

        viewModelScope.launch {
            val track = quranAudioRepository.getTrack(reciterId, surahId)
            val fullAudioUrl =
                track?.audioUrl ?: getFullAudioUrl(targetReciter.baseAudioUrl, surahId)

            val isLocalUrl =
                currentAudioState.currentUrl?.contains("audio/reciters/${reciterId}/") == true
            val isRemoteUrl =
                currentAudioState.currentUrl?.startsWith(targetReciter.baseAudioUrl.trimEnd('/')) == true
            val isSameReciterPlaying = isLocalUrl || isRemoteUrl

            if (isSameReciterPlaying) {
                when (currentAudioState.playbackState) {
                    AudioPlayerState.AudioPlaybackState.PLAYING -> audioPlayerManager.pause()
                    else -> audioPlayerManager.resume()
                }
            } else {
                setReciterLoadingState(reciterId)
                audioPlayerManager.play(fullAudioUrl)
            }
        }
    }

    private fun setReciterLoadingState(reciterId: Int) {
        updateState { state ->
            val updatedList = state.results.map { reciter ->
                if (reciter.id == reciterId) {
                    reciter.copy(playState = PlayState.LOADING)
                } else {
                    reciter.copy(playState = PlayState.RESUME)
                }
            }
            state.copy(results = updatedList)
        }
    }

    private fun getFullAudioUrl(baseUrl: String, surahNumber: Int): String {
        val formattedSurah = surahNumber.toString().padStart(3, '0')
        val cleanBaseUrl = baseUrl.trimEnd('/')
        return "$cleanBaseUrl/$formattedSurah.mp3"
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerManager.release()
    }
}
