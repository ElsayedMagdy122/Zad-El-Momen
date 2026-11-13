package dev.sayed.mehrabalmomen.presentation.screen.radio

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.design_system.component.ToastDetails
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.error
import dev.sayed.mehrabalmomen.designsystem.resources.ic_close_circle
import dev.sayed.mehrabalmomen.designsystem.resources.no_internet_connection
import dev.sayed.mehrabalmomen.domain.analytics.AnalyticsTracker
import dev.sayed.mehrabalmomen.domain.entity.radio.RadioChannel
import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerState
import dev.sayed.mehrabalmomen.domain.model.audio.AudioPlayerStatus
import dev.sayed.mehrabalmomen.domain.model.audio.AudioSource
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.domain.repository.radio.RadioRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class RadioChannelsViewModel(
    private val radioRepository: RadioRepository,
    private val analyticsTracker: AnalyticsTracker
) : BaseViewModel<RadioUiState, RadioChannelsEffect>(RadioUiState()),
    RadioChannelsInteractionListener, KoinComponent {

    val audioPlayer: AudioPlayer by inject(named("radio"))

    init {
        loadCategories()
        observePlayerState()
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            audioPlayer.playerState.collectLatest { serviceState ->
                if (serviceState.status == AudioPlayerStatus.ERROR) {
                    sendEffect(
                        RadioChannelsEffect.ShowToast(
                            ToastDetails(
                                title = Res.string.error,
                                message = Res.string.no_internet_connection,
                                icon = Res.drawable.ic_close_circle
                            )
                        )
                    )
                }
                updateUiBasedOnServiceState(serviceState)
            }
        }
    }

    private fun updateUiBasedOnServiceState(serviceState: AudioPlayerState) {
        updateState { oldState ->
            val updatedChannels = oldState.channels.map { channel ->
                val currentSource = serviceState.currentSource
                val currentUrl = (currentSource as? AudioSource.RemoteUrl)?.url
                val isSelected = channel.streamUrl == currentUrl
                val isPlaying = serviceState.status == AudioPlayerStatus.PLAYING && isSelected
                val isLoading = serviceState.status == AudioPlayerStatus.BUFFERING && isSelected
                channel.copy(
                    isPlaying = isPlaying,
                    selected = isSelected,
                    isLoading = isLoading
                )
            }
            oldState.copy(channels = updatedChannels)
        }
    }

    fun loadCategories() {
        tryToCall(
            onStart = { updateState { it.copy(isLoading = true) } },
            block = { radioRepository.getCategories() },
            onSuccess = { flow ->
                viewModelScope.launch {
                    flow.catch { handleChannelsError() }
                        .collectLatest { categories ->
                            updateState {
                                it.copy(
                                    categories = categories.map { cat ->
                                        RadioUiState.CategoryUi(cat.id, cat.nameAr, cat.nameEn)
                                    },
                                    selectedCategoryId = categories.firstOrNull()?.id ?: ""
                                )
                            }
                            categories.firstOrNull()?.id?.let {
                                getChannelsByCategory(it)
                            }
                        }
                }
            },
            onError = { handleChannelsError() }
        )
    }

    private fun getChannelsByCategory(categoryId: String) {
        analyticsTracker.logEvent(
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
                    flow.catch { handleChannelsError() }
                    .collectLatest { channels ->
                        updateState {
                            it.copy(
                                channels = mapChannelsToUiState(channels),
                                isLoading = false
                            )
                        }
                        updateUiBasedOnServiceState(audioPlayer.playerState.value)
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

                            updateUiBasedOnServiceState(audioPlayer.playerState.value)
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
        analyticsTracker.logEvent(
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
