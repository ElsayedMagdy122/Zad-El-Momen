package dev.sayed.mehrabalmomen.presentation.screen.companion

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.repository.companion.CompanionRepository
import dev.sayed.mehrabalmomen.domain.usecase.ObserveCompanionUseCase
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CompanionViewModel(
    private val observeCompanionUseCase: ObserveCompanionUseCase,
    private val companionRepository: CompanionRepository
) : BaseViewModel<CompanionUiState, Unit>(
    CompanionUiState()
), CompanionInteractionListener {

    private var tasbihJob: Job? = null

    init {
        observeCompanionState()
        observeCompanionEnabled()
        startPeriodicDialogueRefresh()
        startSpiritualActivityTrigger()
    }

    private fun observeCompanionEnabled() {
        viewModelScope.launch {
            companionRepository.observeCompanionEnabled().collect { enabled ->
                updateState { it.copy(isVisible = enabled) }
            }
        }
    }

    private fun observeCompanionState() {
        viewModelScope.launch {
            observeCompanionUseCase().collect { state ->
                val previousMood = screenState.value.mood
                val isDoingActivity = screenState.value.isLaughing || screenState.value.isDoingTasbih
                
                updateState {
                    it.copy(
                        mood = state.mood,
                        dialogueRes = if (it.dialogueRes == null) 
                            CompanionDialogue.getMessage(state) else it.dialogueRes
                    )
                }
                
                if (previousMood != state.mood && !isDoingActivity) {
                    refreshDialogue()
                }
            }
        }
    }

    private fun startPeriodicDialogueRefresh() {
        viewModelScope.launch {
            while(true) {
                delay(600000) // 10 minutes refresh
                if (!screenState.value.isLaughing && !screenState.value.isDoingTasbih) {
                    refreshDialogue()
                }
            }
        }
    }

    private fun startSpiritualActivityTrigger() {
        viewModelScope.launch {
            while(true) {
                delay(2000) 
                val silenceDuration = System.currentTimeMillis() - screenState.value.lastInteractionTime
                if (silenceDuration >= 20000 && !screenState.value.isLaughing && !screenState.value.isDoingTasbih) {
                    tasbihJob = launch { startTasbih() }
                    updateState { it.copy(lastInteractionTime = System.currentTimeMillis()) }
                }
            }
        }
    }

    private suspend fun startTasbih() {
        val tasbihMsgs = listOf(
            R.string.rafiq_tasbih_1, 
            R.string.rafiq_tasbih_2, 
            R.string.rafiq_tasbih_3,
            R.string.rafiq_tasbih_4
        )
        updateState {
            it.copy(
                isDoingTasbih = true,
                dialogueRes = tasbihMsgs.random()
            )
        }
        delay(15000) 
        updateState { it.copy(isDoingTasbih = false) }
        refreshDialogue()
    }

    override fun onInteract() {
        val now = System.currentTimeMillis()
        val currentState = screenState.value

        // Cancel Tasbih if user interacts
        if (currentState.isDoingTasbih) {
            tasbihJob?.cancel()
            updateState { it.copy(isDoingTasbih = false) }
        }

        val newTapCount = if (now - currentState.lastTapTime < 600) {
            currentState.tapCount + 1
        } else {
            1
        }

        updateState { 
            it.copy(
                tapCount = newTapCount,
                lastTapTime = now,
                lastInteractionTime = now
            )
        }

        viewModelScope.launch {
            companionRepository.updateLastInteraction(System.currentTimeMillis())
            if (newTapCount >= 3 && !screenState.value.isLaughing) {
                triggerTickle()
            }
        }
    }

    private fun triggerTickle() {
        viewModelScope.launch {
            updateState {
                it.copy(
                    isLaughing = true,
                    dialogueRes = R.string.rafiq_tickle
                )
            }
            delay(3500) 
            updateState { 
                it.copy(
                    isLaughing = false,
                    tapCount = 0
                ) 
            }
            refreshDialogue()
        }
    }

    fun refreshDialogue() {
        viewModelScope.launch {
            // Guard against overwriting special activities
            if (screenState.value.isLaughing || screenState.value.isDoingTasbih) return@launch

            val state = observeCompanionUseCase().first()
            updateState {
                it.copy(dialogueRes = CompanionDialogue.getMessage(state))
            }
        }
    }
}
