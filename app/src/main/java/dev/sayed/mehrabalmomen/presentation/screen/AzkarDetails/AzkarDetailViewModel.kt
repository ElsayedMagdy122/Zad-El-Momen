package dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarItem
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
) : BaseViewModel<AzkarDetailUiState, AzkarDetailEffect>(AzkarDetailUiState()), AzkarDetailInteractionListener {

    fun loadAzkar(title: String) {
        updateState { it.copy(isLoading = true, title = title) }
        tryToCall(
            block = {
                val category = repository.getAzkarCategories().find { it.title == title }
                val items = category?.items ?: emptyList()
                val completedToday = repository.getCompletedItemsToday()
                Pair(items, completedToday)
            },
            onSuccess = { (items, completedToday) ->
                val initialCounts = items.mapIndexed { index, item ->
                    val isCompleted = completedToday.contains(item.content)
                    index to if (isCompleted) 0 else (item.count.toIntOrNull() ?: 1)
                }.toMap()
                updateState {
                    it.copy(
                        items = items,
                        itemCounts = initialCounts,
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

    override fun onClickBack() {
        sendEffect(AzkarDetailEffect.NavigateBack)
    }

    override fun onDecrementCount(index: Int) {
        val current = screenState.value.itemCounts[index] ?: 1
        if (current > 0) {
            val updatedCount = current - 1
            val updatedCounts = screenState.value.itemCounts + (index to updatedCount)
            updateState { it.copy(itemCounts = updatedCounts) }

            if (updatedCount == 0) {
                val item = screenState.value.items.getOrNull(index)
                if (item != null) {
                    viewModelScope.launch {
                        try {
                            repository.recordItemCompleted(item.content)
                        } catch (_: Exception) {}
                    }
                }
            }
        }
    }

    override fun onOpenAddDhikrBottomSheet() {
        updateState { it.copy(isAddDhikrBottomSheetOpen = true) }
    }

    override fun onDismissAddDhikrBottomSheet() {
        updateState { 
            it.copy(
                isAddDhikrBottomSheetOpen = false,
                newContent = "",
                newCount = "1",
                newDescription = "",
                newReference = ""
            ) 
        }
    }

    override fun onNewContentChanged(v: String) {
        updateState { it.copy(newContent = v) }
    }

    override fun onNewCountChanged(v: String) {
        updateState { it.copy(newCount = v) }
    }

    override fun onNewDescriptionChanged(v: String) {
        updateState { it.copy(newDescription = v) }
    }

    override fun onNewReferenceChanged(v: String) {
        updateState { it.copy(newReference = v) }
    }

    override fun onSaveNewDhikr() {
        val currentState = screenState.value
        val content = currentState.newContent.trim()
        val count = currentState.newCount.trim().ifEmpty { "1" }
        val description = currentState.newDescription.trim()
        val reference = currentState.newReference.trim()
        val categoryTitle = currentState.title

        if (content.isNotEmpty() && categoryTitle.isNotEmpty()) {
            val newItem = AzkarItem(
                content = content,
                count = count,
                description = description,
                reference = reference
            )
            tryToCall(
                block = { repository.addDhikr(categoryTitle, newItem) },
                onSuccess = {
                    onDismissAddDhikrBottomSheet()
                    loadAzkar(categoryTitle)
                },
                onError = {}
            )
        }
    }

    override fun onPlayAll() {
        val resetCounts = screenState.value.items.mapIndexed { index, item ->
            index to (item.count.toIntOrNull() ?: 1)
        }.toMap()
        updateState { it.copy(itemCounts = resetCounts) }
    }
}
