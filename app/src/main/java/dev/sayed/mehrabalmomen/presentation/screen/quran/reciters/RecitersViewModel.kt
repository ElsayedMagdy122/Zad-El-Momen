package dev.sayed.mehrabalmomen.presentation.screen.quran.reciters

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioReadersRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.quran.audio_utils.AudioPlayerManager
import dev.sayed.mehrabalmomen.presentation.screen.quran.audio_utils.AudioPlayerState
import dev.sayed.mehrabalmomen.presentation.utils.toUiErrorMessage
import kotlinx.coroutines.launch

class RecitersViewModel(
    private val readersRepository: QuranAudioReadersRepository,
    private val audioPlayerManager: AudioPlayerManager,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<RecitersUiState, RecitersEffect>(RecitersUiState()) {

    private val surahId: Int = savedStateHandle.toRoute<Route.RecitersScreen>().surahId


    init {
        observeAudioState()
    }

    fun onReciterSelected(readerId: Int, nameAr: String, nameEn: String) {
        sendEffect(
            RecitersEffect.ReciterSelected(
                readerId = readerId,
                nameAr = nameAr,
                nameEn = nameEn
            )
        )
    }

    fun loadReciters(isArabic: Boolean = true) {
        tryToCall(
            onStart = { updateState { it.copy(isLoading = true) } },
            block = {
                readersRepository.getReaders()
            },
            onSuccess = { readersList ->
                updateState { state ->
                    state.copy(
                        reciters = readersList.map { reader -> reader.toUiState(isArabic) },
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

    private fun observeAudioState() {
        viewModelScope.launch {
            audioPlayerManager.playerState.collect { audioState ->
                updateState { state ->
                    val updatedReciters = state.reciters.map { reciter ->
                        val isCurrentReciter = audioState.currentUrl?.startsWith(
                            reciter.baseAudioUrl.trimEnd('/')
                        ) == true

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
        val fullAudioUrl = getFullAudioUrl(targetReciter.baseAudioUrl, surahId)

        val isSameReciterPlaying = currentAudioState.currentUrl?.startsWith(
            targetReciter.baseAudioUrl.trimEnd('/')
        ) == true

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