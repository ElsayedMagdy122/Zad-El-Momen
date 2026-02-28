package dev.sayed.mehrabalmomen.presentation.screen.radio

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.radio.RadioRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.launch

class RadioChannelsViewModel(
    private val radioRepository: RadioRepository,
    private val mediaRepository: MediaRepository,
    private val networkConnectionRepository: NetworkConnectionRepository
) :
    BaseViewModel<RadioUiState, RadioChannelsEffect>(RadioUiState()), RadioChannelsInteractionListener {

    init {
        getRadioChannels()
    }
    fun getRadioChannels() {
        viewModelScope.launch {

            val isConnected = networkConnectionRepository.isCurrentlyConnected()
            if (!isConnected) {
                updateState {
                    it.copy(
                        isLoading = false,
                        isNoInternet = true
                    )
                }
                return@launch
            }

            tryToCall(
                onStart = {
                    updateState {
                        it.copy(
                            isLoading = screenState.value.channels.isEmpty(),
                            isNoInternet = false
                        )
                    }
                },
                block = {
                    radioRepository.getAllChannels()
                },
                onSuccess = { flow ->
                    flow.collect { channels ->

                        val currentUrl =
                            (mediaRepository as MediaRepositoryImpl).currentUrl()
                        val isPlaying = mediaRepository.isPlaying()

                        updateState {
                            it.copy(
                                channels = channels.map { channel ->
                                    val isCurrent =
                                        channel.streamUrl == currentUrl && isPlaying

                                    RadioUiState.RadioChannelUiState(
                                        id = channel.id,
                                        nameAr = channel.nameAr,
                                        nameEn = channel.nameEn,
                                        streamUrl = channel.streamUrl,
                                        isPlaying = isCurrent,
                                        selected = isCurrent
                                    )
                                },
                                isLoading = false,
                                isNoInternet = false
                            )
                        }
                    }
                },
                onError = { exception ->

                    val hasData = screenState.value.channels.isNotEmpty()

                    updateState {
                        it.copy(
                            isLoading = false,
                            isNoInternet = !hasData
                        )
                    }


                    if (hasData) {
                        sendEffect(
                           RadioChannelsEffect.ShowToast(
                                ToastDetails(
                                    title = R.string.error,
                              //      message = R.string.something_went_wrong,
                                    message = R.string.error,
                                    icon = R.drawable.ic_close_circle
                                )
                            )
                        )
                    }
                }
            )
        }
    }
    fun getRadioChannels1() {
        viewModelScope.launch {
            val isConnected = networkConnectionRepository.isCurrentlyConnected()
            if (!isConnected) {
                updateState {
                    it.copy(
                        isLoading = false,
                        isNoInternet = true
                    )
                }
                return@launch
            }
        tryToCall(
            block = {
                radioRepository.getAllChannels()
            },
            onSuccess = { flow ->
                flow.collect { channels ->

                    val currentUrl =
                        (mediaRepository as MediaRepositoryImpl).currentUrl()
                    val isPlaying = mediaRepository.isPlaying()

                    updateState {
                        it.copy(
                            channels = channels.map { channel ->

                                val isCurrent =
                                    channel.streamUrl == currentUrl && isPlaying

                                RadioUiState.RadioChannelUiState(
                                    id = channel.id,
                                    nameAr = channel.nameAr,
                                    nameEn = channel.nameEn,
                                    streamUrl = channel.streamUrl,
                                    isPlaying = isCurrent,
                                    selected = isCurrent
                                )
                            },
                            isLoading = false
                        )
                    }
                }
            },
            onError = {}
        )
    }}

    override fun onChannelClick(id: Int) {
        updateState { state ->
            state.copy(
                channels = state.channels.map {
                    it.copy(selected = it.id == id)
                }
            )
        }
    }

    override fun onPlayPauseClick(id: Int) {

        val selectedChannel = screenState.value.channels.find { it.id == id } ?: return

        val isCurrentlyPlaying = selectedChannel.isPlaying
        viewModelScope.launch {
            if (isCurrentlyPlaying) {
                mediaRepository.pause()
            } else {

                mediaRepository.play(selectedChannel.streamUrl)
            }
        }

        updateState { state ->
            state.copy(
                channels = state.channels.map {
                    when (it.id) {
                        id -> it.copy(
                            selected = true,
                            isPlaying = !isCurrentlyPlaying
                        )

                        else -> it.copy(
                            selected = false,
                            isPlaying = false
                        )
                    }
                }
            )
        }
    }
}