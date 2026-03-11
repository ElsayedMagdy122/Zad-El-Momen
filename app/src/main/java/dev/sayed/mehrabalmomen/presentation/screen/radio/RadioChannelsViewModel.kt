package dev.sayed.mehrabalmomen.presentation.screen.radio

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.entity.radio.RadioChannel
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

    init {
        getRadioChannels()
        observePlayerState()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            playerController.playerState.collectLatest { serviceState ->
                if (serviceState.isError) {
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
                updateUiBasedOnServiceState(serviceState)
            }
        }
    }

    private fun updateUiBasedOnServiceState(serviceState: PlayerState) {
        updateState { oldState ->
            oldState.copy(
                channels = oldState.channels.map { channel ->
                    val isSelected = channel.streamUrl == serviceState.currentUrl
                    channel.copy(
                        isPlaying = serviceState.isPlaying && isSelected,
                        selected = isSelected
                    )
                }
            )
        }
    }

    fun getRadioChannels() {
        tryToCall(
            onStart = { updateState { it.copy(isLoading = it.channels.isEmpty()) } },
            block = { radioRepository.getAllChannels() },
            onSuccess = { flow ->
                viewModelScope.launch {
                    flow.catch { handleChannelsError() }
                        .collectLatest { channels ->
                            val uiChannels = mapChannelsToUiState(channels)
                            updateState { it.copy(channels = uiChannels, isLoading = false) }

                            updateUiBasedOnServiceState(playerController.playerState.value)
                        }
                }
            },
            onError = { handleChannelsError() }
        )
    }

    private fun mapChannelsToUiState(channels: List<RadioChannel>) = channels.map {
        RadioUiState.RadioChannelUiState(it.id, it.nameAr, it.nameEn, it.streamUrl)
    }

    private fun handleChannelsError() =
        updateState { it.copy(isNoInternet = true, isLoading = false) }

    override fun onPlayClick(id: Int) {
        val channel = screenState.value.channels.firstOrNull { it.id == id } ?: return
        sendEffect(RadioChannelsEffect.PlaySound(channel.streamUrl, channel.nameAr))
    }

    override fun onPauseClick(id: Int) {
        sendEffect(RadioChannelsEffect.PauseSound)
    }
}