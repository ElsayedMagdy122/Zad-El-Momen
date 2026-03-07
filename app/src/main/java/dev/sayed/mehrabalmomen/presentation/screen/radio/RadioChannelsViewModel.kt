package dev.sayed.mehrabalmomen.presentation.screen.radio

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.entity.radio.RadioChannel
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.radio.RadioRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerController
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class RadioChannelsViewModel(
    private val radioRepository: RadioRepository,
    private val playerController: PlayerController
) : BaseViewModel<RadioUiState, RadioChannelsEffect>(RadioUiState()),
    RadioChannelsInteractionListener {
    private var lastUrl: String? = null
    private var wasPlaying = false
    init {
        getRadioChannels()
        observePlayerState()
    }


    private fun observePlayerState() {
        viewModelScope.launch {
            playerController.playerState.collectLatest { serviceState ->

                val stoppedUnexpectedly =
                    wasPlaying &&
                            !serviceState.isPlaying &&
                            serviceState.currentUrl != null &&
                            serviceState.currentUrl == lastUrl

                if (stoppedUnexpectedly) {
                    sendEffect(
                        RadioChannelsEffect.ShowToast(
                            ToastDetails(
                                title = R.string.error,
                                message = R.string.no_internet_connection,
                                icon = R.drawable.ic_close_circle
                            )
                        )
                    )
                }

                lastUrl = serviceState.currentUrl
                wasPlaying = serviceState.isPlaying

                updateUiBasedOnServiceState(serviceState)
            }
        }
    }

    private fun updateUiBasedOnServiceState(serviceState: PlayerState) {
        val currentPlayingUrl = serviceState.currentUrl
        updateState { oldState ->
            oldState.copy(
                channels = oldState.channels.map { channel ->
                    channel.copy(
                        isPlaying = serviceState.isPlaying && channel.streamUrl == currentPlayingUrl,
                        selected = channel.streamUrl == currentPlayingUrl
                    )
                }
            )
        }
    }

    fun getRadioChannels1() {
        tryToCall(
            onStart = {
                updateState {
                    it.copy(
                        isLoading = screenState.value.channels.isEmpty(),
                        isNoInternet = false
                    )
                }
            },
            block = { radioRepository.getAllChannels() },
            onSuccess = { flow ->
                viewModelScope.launch {
                    flow
                        .catch {
                            updateState {
                                it.copy(
                                    isNoInternet = true,
                                    isLoading = false
                                )
                            }}
                        .collectLatest { channels ->
                            updateState {
                                it.copy(
                                    channels = channels.map { channel ->
                                        RadioUiState.RadioChannelUiState(
                                            id = channel.id,
                                            nameAr = channel.nameAr,
                                            nameEn = channel.nameEn,
                                            streamUrl = channel.streamUrl
                                        )
                                    },
                                    isLoading = false,
                                    isNoInternet = false
                                )
                            }
                        }
                }
            },
            onError = { error ->
                updateState { it.copy(isNoInternet = true, isLoading = false) }
            }
        )
    }
    fun getRadioChannels() {
        tryToCall(
            onStart = { handleLoadingState() },
            block = { radioRepository.getAllChannels() },
            onSuccess = { flow -> collectChannels(flow) },
            onError = { handleChannelsError() }
        )
    }
    private fun handleLoadingState() {
        updateState {
            it.copy(
                isLoading = screenState.value.channels.isEmpty(),
                isNoInternet = false
            )
        }
    }
    private fun collectChannels(flow: kotlinx.coroutines.flow.Flow<List<RadioChannel>>) {
        viewModelScope.launch {
            flow
                .catch { handleChannelsError() }
                .collectLatest { channels ->
                    updateState {
                        it.copy(
                            channels = mapChannelsToUiState(channels),
                            isLoading = false,
                            isNoInternet = false
                        )
                    }
                }
        }
    }
    private fun mapChannelsToUiState(
        channels: List<RadioChannel>
    ): List<RadioUiState.RadioChannelUiState> {
        return channels.map { channel ->
            RadioUiState.RadioChannelUiState(
                id = channel.id,
                nameAr = channel.nameAr,
                nameEn = channel.nameEn,
                streamUrl = channel.streamUrl
            )
        }
    }
    private fun handleChannelsError() {
        updateState {
            it.copy(
                isNoInternet = true,
                isLoading = false
            )
        }
    }
    override fun onPlayClick(id: Int) {
        val channel = screenState.value.channels.firstOrNull { it.id == id } ?: return
        sendEffect(RadioChannelsEffect.PlaySound(channel.streamUrl, channel.nameAr))
        updateState {
            it.copy(
                channels = it.channels.map { ch ->
                    ch.copy(
                        isPlaying = ch.id == id,
                        selected = ch.id == id
                    )
                }
            )
        }
    }

    override fun onPauseClick(id: Int) {
        sendEffect(RadioChannelsEffect.PauseSound)
        updateState {
            it.copy(
                channels = it.channels.map { ch ->
                    if (ch.id == id) ch.copy(isPlaying = false, selected = false) else ch
                }
            )
        }
    }
}