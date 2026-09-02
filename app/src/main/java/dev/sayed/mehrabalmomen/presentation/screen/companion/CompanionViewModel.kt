package dev.sayed.mehrabalmomen.presentation.screen.companion

import androidx.lifecycle.viewModelScope
import dev.sayed.mehrabalmomen.domain.model.companion.CompanionMood
import dev.sayed.mehrabalmomen.domain.model.companion.CompanionState
import dev.sayed.mehrabalmomen.domain.repository.companion.CompanionRepository
import dev.sayed.mehrabalmomen.domain.usecase.GetCompanionMessageUseCase
import dev.sayed.mehrabalmomen.domain.usecase.GetCompanionMessageUseCase.CompanionMessage
import dev.sayed.mehrabalmomen.domain.usecase.ObserveCompanionUseCase
import dev.sayed.mehrabalmomen.presentation.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class CompanionViewModel(
    private val observeCompanionUseCase: ObserveCompanionUseCase,
    private val getCompanionMessage: GetCompanionMessageUseCase,
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
            observeCompanionUseCase().collect { (state, mood) ->
                val previousMood = screenState.value.mood
                val isDoingActivity = screenState.value.isLaughing || screenState.value.isDoingTasbih
                
                updateState {
                    it.copy(
                        mood = mood,
                        dialogueRes = if (it.dialogueRes == null) {
                            val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
                            val message = getCompanionMessage.execute(state, hour)
                            CompanionDialogue.getMessageRes(message)
                        } else it.dialogueRes
                    )
                }
                
                if (previousMood != mood && !isDoingActivity) {
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
                val now = Clock.System.now().toEpochMilliseconds()
                val silenceDuration = now - screenState.value.lastInteractionTime
                if (silenceDuration >= 20000 && !screenState.value.isLaughing && !screenState.value.isDoingTasbih) {
                    tasbihJob = launch { startTasbih() }
                    updateState { it.copy(lastInteractionTime = now) }
                }
            }
        }
    }

    private suspend fun startTasbih() {
        val tasbihMsgs = listOf(
            CompanionMessage.TASBIH_1, 
            CompanionMessage.TASBIH_2, 
            CompanionMessage.TASBIH_3,
            CompanionMessage.TASBIH_4
        )
        updateState {
            it.copy(
                isDoingTasbih = true,
                dialogueRes = CompanionDialogue.getMessageRes(tasbihMsgs.random())
            )
        }
        delay(15000) 
        updateState { it.copy(isDoingTasbih = false) }
        refreshDialogue()
    }

    override fun onInteract() {
        val now = Clock.System.now().toEpochMilliseconds()
        val currentState = screenState.value

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
            companionRepository.updateLastInteraction(now)
            
            if (newTapCount < 3) {
                val (state, _) = observeCompanionUseCase().first()
                val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
                val message = getCompanionMessage.execute(state, hour, isManualInteraction = true)
                updateState { it.copy(dialogueRes = CompanionDialogue.getMessageRes(message)) }
            } else if (!screenState.value.isLaughing) {
                triggerTickle()
            }
        }
    }

    private fun triggerTickle() {
        viewModelScope.launch {
            updateState {
                it.copy(
                    isLaughing = true,
                    dialogueRes = CompanionDialogue.getMessageRes(CompanionMessage.TICKLE)
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
            if (screenState.value.isLaughing || screenState.value.isDoingTasbih) return@launch

            val (state, _) = observeCompanionUseCase().first()
            val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
            val message = getCompanionMessage.execute(state, hour)
            updateState {
                it.copy(dialogueRes = CompanionDialogue.getMessageRes(message))
            }
        }
    }
}
