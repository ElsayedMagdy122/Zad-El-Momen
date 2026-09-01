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
import dev.sayed.mehrabalmomen.domain.exceptions.NetworkException
import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerStatus
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.utils.toUiErrorMessage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class RecitersViewModel(
    private val readersRepository: QuranAudioReadersRepository,
    private val quranAudioRepository: QuranAudioRepository,
    private val recitationPreferences: RecitationPreferences,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<RecitersUiState, RecitersEffect>(RecitersUiState()), KoinComponent {

    private val audioPlayer: AudioPlayer by inject(named("quran"))

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

    fun loadReciters(isArabic: Boolean = true, isManualRetry: Boolean = false) {
        tryToCall(
            onStart = {
                updateState {
                    it.copy(
                        isLoading = true,
                        isNoInternet = if (isManualRetry) it.isNoInternet else false,
                        isError = false
                    )
                }
            },
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
                    val allDownloaded = readersRepository.getDownloadedRecitersOnce()
                    
                    val readersWithCurrentSurah = allDownloaded.filter { reader ->
                        readersRepository.isReciterDownloaded(reader.id, surahId)
                    }
                    
                    if (readersWithCurrentSurah.isEmpty()) throw e

                    allReaders = readersWithCurrentSurah
                    readersWithCurrentSurah.map { reader ->
                        reader.toUiState(
                            isArabic,
                            DownloadState.DOWNLOADED,
                            isSelected = reader.id == currentReaderId
                        )
                    }
                }
            },
            onSuccess = { readersList ->
                updateState { state ->
                    state.copy(
                        reciters = readersList,
                        isLoading = false,
                        isNoInternet = false,
                        isError = false
                    )
                }
            },
            onError = { throwable ->
                val isNetworkError = throwable is NetworkException
                updateState {
                    it.copy(
                        isLoading = false,
                        isNoInternet = isNetworkError && isManualRetry,
                        isError = !isNetworkError
                    )
                }

                if (!isNetworkError) {
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
                val workState = workInfo.state

                updateState { currentState ->
                    currentState.copy(
                        reciters = currentState.reciters.map {
                            if (it.id == reciterId) {
                                val downloadState = when {
                                    workState.isFinished && workState == androidx.work.WorkInfo.State.SUCCEEDED -> DownloadState.DOWNLOADED
                                    workState.isFinished -> DownloadState.FAILED
                                    else -> DownloadState.DOWNLOADING
                                }
                                it.copy(
                                    downloadProgress = progress,
                                    downloadState = downloadState
                                )
                            } else it
                        }
                    )
                }
            }
        }
    }

    private fun observeAudioState() {
        viewModelScope.launch {
            audioPlayer.playerState.collect { audioState ->
                updateState { state ->
                    val currentSource = audioState.currentSource
                    val currentUrl = when (currentSource) {
                        is AudioSource.RemoteUrl -> currentSource.url
                        is AudioSource.LocalFile -> "file://${currentSource.path}"
                        else -> null
                    }
                    val updatedReciters = state.reciters.map { reciter ->
                        val isLocalUrl =
                            currentUrl?.contains("audio/reciters/${reciter.id}/") == true
                        val isRemoteUrl = currentUrl?.startsWith(
                            reciter.baseAudioUrl.trimEnd('/')
                        ) == true
                        val isCurrentReciter = isLocalUrl || isRemoteUrl

                        if (isCurrentReciter) {
                            val newPlayState = when (audioState.status) {
                                AudioPlayerStatus.BUFFERING -> PlayState.LOADING
                                AudioPlayerStatus.PLAYING -> PlayState.PLAY
                                AudioPlayerStatus.PAUSED,
                                AudioPlayerStatus.ENDED,
                                AudioPlayerStatus.IDLE,
                                AudioPlayerStatus.ERROR -> PlayState.RESUME
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
        val currentAudioState = audioPlayer.playerState.value
        val currentSource = currentAudioState.currentSource
        val currentUrl = when (currentSource) {
            is AudioSource.RemoteUrl -> currentSource.url
            is AudioSource.LocalFile -> "file://${currentSource.path}"
            else -> null
        }

        viewModelScope.launch {
            val track = quranAudioRepository.getTrack(reciterId, surahId)
            val fullAudioUrl =
                track?.audioUrl ?: getFullAudioUrl(targetReciter.baseAudioUrl, surahId)

            val isLocalUrl =
                currentUrl?.contains("audio/reciters/${reciterId}/") == true
            val isRemoteUrl = currentUrl?.startsWith(
                targetReciter.baseAudioUrl.trimEnd('/')
            ) == true
            val isSameReciterPlaying = isLocalUrl || isRemoteUrl

            if (isSameReciterPlaying) {
                when (currentAudioState.status) {
                    AudioPlayerStatus.PLAYING -> {
                        audioPlayer.pause()
                    }

                    AudioPlayerStatus.PAUSED, AudioPlayerStatus.ENDED, AudioPlayerStatus.IDLE -> {
                        audioPlayer.resume()
                    }

                    else -> {
                        setReciterLoadingState(reciterId)
                        audioPlayer.play(AudioSource.fromPath(fullAudioUrl))
                    }
                }
            } else {
                setReciterLoadingState(reciterId)
                audioPlayer.play(AudioSource.fromPath(fullAudioUrl))
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
        audioPlayer.release()
    }
}