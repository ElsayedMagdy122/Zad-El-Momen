package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.data.settings.local.RecitationPreferences
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioReader
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioReadersRepository
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.quran.audio_utils.AudioPlayerManager
import dev.sayed.mehrabalmomen.presentation.screen.quran.audio_utils.AudioPlayerState
import dev.sayed.mehrabalmomen.presentation.utils.toUiErrorMessage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RecitersViewModel(
    private val readersRepository: QuranAudioReadersRepository,
    private val quranAudioRepository: QuranAudioRepository,
    private val audioPlayerManager: AudioPlayerManager,
    private val recitationPreferences: RecitationPreferences,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<RecitersUiState, RecitersEffect>(RecitersUiState()) {

    private val surahId: Int = savedStateHandle.toRoute<Route.RecitersScreen>().surahId
    private val currentReaderId: Int? =
        savedStateHandle.toRoute<Route.RecitersScreen>().currentReaderId
    private var allReaders: List<QuranAudioReader> = emptyList()

    init {
        observeAudioState()
        observeDownloadedReciters()
    }

    fun onReciterSelected(readerId: Int) {
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
                    reciters = state.reciters.map {
                        it.copy(isSelected = it.id == readerId)
                    }
                )
            }
            sendEffect(RecitersEffect.NavigateBack)
        }
    }

    fun loadReciters(isArabic: Boolean = true) {
        tryToCall(
            onStart = { updateState { it.copy(isLoading = true) } },
            block = {
                try {
                    allReaders = readersRepository.getReaders()
                    allReaders.map { reader ->
                        val isDownloaded =
                            if (readersRepository.isReciterDownloaded(reader.id, surahId)) {
                                DownloadState.DOWNLOADED
                            } else {
                                DownloadState.NOT_DOWNLOADED
                            }
                        reader.toUiState(
                            isArabic,
                            isDownloaded,
                            isSelected = reader.id == currentReaderId
                        )
                    }
                } catch (e: Exception) {
                    val downloaded = readersRepository.getDownloadedRecitersOnce()
                    if (downloaded.isEmpty()) throw e
                    allReaders = downloaded
                    downloaded.map {
                        it.toUiState(
                            isArabic,
                            DownloadState.DOWNLOADED,
                            isSelected = it.id == currentReaderId
                        )
                    }
                }
            },
            onSuccess = { readersList ->
                updateState { state ->
                    state.copy(
                        reciters = readersList,
                        isLoading = false
                    )
                }
            },
            onError = { throwable ->
                updateState { it.copy(isLoading = false) }
                val errorMessageRes = throwable.toUiErrorMessage()

                sendEffect(
                    RecitersEffect.ShowToast(
                        ToastDetails(
                            title = R.string.error,
                            message = errorMessageRes,
                            icon = R.drawable.ic_close_circle
                        )
                    )
                )
            }
        )
    }

    private fun observeDownloadedReciters() {
        viewModelScope.launch {
            readersRepository.getDownloadedReciters(surahId).collectLatest { downloaded ->
                updateState { state ->
                    val updatedReciters = state.reciters.map { reciter ->
                        val isDownloaded =
                            downloaded.any { it.id == reciter.id }
                        if (isDownloaded) {
                            reciter.copy(downloadState = DownloadState.DOWNLOADED)
                        } else {
                            reciter
                        }
                    }
                    state.copy(reciters = updatedReciters)
                }
            }
        }
    }

    fun onDownloadClick(reciterId: Int) {
        val domainReciter = allReaders.find { it.id == reciterId } ?: return
        readersRepository.downloadReciter(domainReciter, surahId)

        updateState { state ->
            state.copy(
                reciters = state.reciters.map {
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
                        reciters = state.reciters.map {
                            if (it.id == reciterId) it.copy(downloadProgress = progress) else it
                        }
                    )
                }
            }
        }
    }

    private fun observeAudioState() {
        viewModelScope.launch {
            audioPlayerManager.playerState.collect { audioState ->
                updateState { state ->
                    val updatedReciters = state.reciters.map { reciter ->
                        val isLocalUrl =
                            audioState.currentUrl?.contains("audio/reciters/${reciter.id}/") == true
                        val isRemoteUrl = audioState.currentUrl?.startsWith(
                            reciter.baseAudioUrl.trimEnd('/')
                        ) == true
                        val isCurrentReciter = isLocalUrl || isRemoteUrl

                        if (isCurrentReciter) {
                            val newPlayState = when (audioState.playbackState) {
                                AudioPlayerState.AudioPlaybackState.BUFFERING -> PlayState.LOADING
                                AudioPlayerState.AudioPlaybackState.PLAYING -> PlayState.PLAY
                                AudioPlayerState.AudioPlaybackState.PAUSED,
                                AudioPlayerState.AudioPlaybackState.ENDED,
                                AudioPlayerState.AudioPlaybackState.IDLE,
                                AudioPlayerState.AudioPlaybackState.ERROR -> PlayState.RESUME
                            }
                            reciter.copy(playState = newPlayState)
                        } else {
                            reciter.copy(playState = PlayState.RESUME)
                        }
                    }
                    state.copy(reciters = updatedReciters)
                }
            }
        }
    }

    fun onPlayClick(reciterId: Int) {
        val targetReciter = screenState.value.reciters.find { it.id == reciterId } ?: return
        val currentAudioState = audioPlayerManager.playerState.value

        viewModelScope.launch {
            val track = quranAudioRepository.getTrack(reciterId, surahId)
            val fullAudioUrl =
                track?.audioUrl ?: getFullAudioUrl(targetReciter.baseAudioUrl, surahId)

            val isLocalUrl =
                currentAudioState.currentUrl?.contains("audio/reciters/${reciterId}/") == true
            val isRemoteUrl = currentAudioState.currentUrl?.startsWith(
                targetReciter.baseAudioUrl.trimEnd('/')
            ) == true
            val isSameReciterPlaying = isLocalUrl || isRemoteUrl

            if (isSameReciterPlaying) {
                when (currentAudioState.playbackState) {
                    AudioPlayerState.AudioPlaybackState.PLAYING -> {
                        audioPlayerManager.pause()
                    }

                    AudioPlayerState.AudioPlaybackState.PAUSED, AudioPlayerState.AudioPlaybackState.ENDED, AudioPlayerState.AudioPlaybackState.IDLE -> {
                        audioPlayerManager.resume()
                    }

                    else -> {
                        setReciterLoadingState(reciterId)
                        audioPlayerManager.play(fullAudioUrl)
                    }
                }
            } else {
                setReciterLoadingState(reciterId)
                audioPlayerManager.play(fullAudioUrl)
            }
        }
    }

    private fun setReciterLoadingState(reciterId: Int) {
        updateState { state ->
            val updatedList = state.reciters.map { reciter ->
                if (reciter.id == reciterId) {
                    reciter.copy(playState = PlayState.LOADING)
                } else {
                    reciter.copy(playState = PlayState.RESUME)
                }
            }
            state.copy(reciters = updatedList)
        }
    }

    fun onClickSearch() {
        sendEffect(RecitersEffect.NavigateToRecitersSearch(surahId, currentReaderId))
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