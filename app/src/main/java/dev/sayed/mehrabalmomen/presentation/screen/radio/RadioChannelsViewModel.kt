package dev.sayed.mehrabalmomen.presentation.screen.radio

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.domain.entity.radio.RadioChannel
import dev.sayed.mehrabalmomen.domain.repository.radio.RadioRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerController
import dev.sayed.mehrabalmomen.presentation.screen.radio.player.PlayerState
import dev.sayed.mehrabalmomen.presentation.utils.AnalyticsHelper
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class RadioChannelsViewModel(
    private val radioRepository: RadioRepository,
    private val playerController: PlayerController,
    private val analyticsHelper: AnalyticsHelper
) : BaseViewModel<RadioUiState, RadioChannelsEffect>(RadioUiState()),
    RadioChannelsInteractionListener {

    init {
        loadCategories()
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

            val updatedChannels = oldState.channels.map { channel ->

                val isSelected = channel.streamUrl == serviceState.currentUrl
                val isPlaying = serviceState.isPlaying && isSelected

                val isLoading =
                    if (!isSelected) false
                    else if (isPlaying) false
                    else channel.isLoading || serviceState.isBuffering

                if (
                    channel.isPlaying == isPlaying &&
                    channel.selected == isSelected &&
                    channel.isLoading == isLoading
                ) {
                    channel
                } else {
                    channel.copy(
                        isPlaying = isPlaying,
                        selected = isSelected,
                        isLoading = isLoading
                    )
                }
            }

            oldState.copy(channels = updatedChannels)
        }
    }

    private fun loadCategories() {
        tryToCall(
            block = { radioRepository.getCategories() },
            onSuccess = { flow ->
                viewModelScope.launch {
                    flow.collectLatest { categories ->

                        val default = categories.firstOrNull {
                            it.nameEn == "Quran"
                        }?.toUi()

                        updateState {
                            it.copy(
                                categories = categories.map { it.toUi() },
                                selectedCategoryId = default?.id
                            )
                        }

                        default?.id?.let { getChannelsByCategory(it) }
                    }
                }
            },
            onError = {}
        )
    }

    private fun getChannelsByCategory(categoryId: String) {
        analyticsHelper.logEvent(
            name = "on click category",
            params = mapOf(
                "category_id" to categoryId
            )
        )
        tryToCall(
            onStart = { updateState { it.copy(isLoading = true) } },
            block = { radioRepository.getChannelsByCategory(categoryId) },
            onSuccess = { flow ->
                viewModelScope.launch {
                    flow.collectLatest { channels ->
                        updateState {
                            it.copy(
                                channels = mapChannelsToUiState(channels),
                                isLoading = false
                            )
                        }
                        updateUiBasedOnServiceState(playerController.playerState.value)
                    }
                }
            },
            onError = { handleChannelsError() }
        )
    }

    fun onCategorySelected(categoryId: String) {
        updateState { it.copy(selectedCategoryId = categoryId) }
        getChannelsByCategory(categoryId)
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

    private fun mapChannelsToUiState(channels: List<RadioChannel>) = channels.shuffled().map {
        RadioUiState.RadioChannelUiState(it.id, it.nameAr, it.nameEn, it.streamUrl)
    }

    private fun handleChannelsError() =
        updateState { it.copy(isNoInternet = true, isLoading = false) }

    override fun onPlayClick(id: Int) {
        val channel = screenState.value.channels.firstOrNull { it.id == id } ?: return
        analyticsHelper.logEvent(
            name = "on click play",
            params = mapOf(
                "channel_name" to channel.nameAr
            )
        )
        sendEffect(RadioChannelsEffect.PlaySound(channel.streamUrl, channel.nameAr))
    }

    override fun onPauseClick(id: Int) {
        updateState { old ->
            old.copy(
                channels = old.channels.map {
                    if (it.id == id) it.copy(isPlaying = false)
                    else it
                }
            )
        }
        sendEffect(RadioChannelsEffect.PauseSound)
    }
}