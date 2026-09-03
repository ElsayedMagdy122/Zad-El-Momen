package dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.repository.azkar.AzkarRepository
import dev.sayed.mehrabalmomen.domain.repository.companion.CompanionRepository
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class AzkarDetailViewModel(
    private val repository: AzkarRepository,
    private val companionRepository: CompanionRepository
) : BaseViewModel<AzkarDetailUiState, AzkarDetailEffect>(AzkarDetailUiState()) {

    fun loadAzkar(title: String) {
        updateState { it.copy(isLoading = true, title = title) }
        tryToCall(
            block = { repository.getAzkarCategories().find { it.title == title }?.items ?: emptyList() },
            onSuccess = { items ->
                updateState {
                    it.copy(
                        items = items,
                        isLoading = false
                    )
                }
                viewModelScope.launch {
                    companionRepository.updateAzkarReadStatus(true)
                    companionRepository.updateLastInteraction(Clock.System.now().toEpochMilliseconds())
                }
            },
            onError = {
                updateState { it.copy(isLoading = false) }
            }
        )
    }

    fun onClickBack() {
        sendEffect(AzkarDetailEffect.NavigateBack)
    }
}
