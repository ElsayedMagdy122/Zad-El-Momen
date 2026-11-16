package dev.sayed.mehrabalmomen.presentation.screen.companion

import dev.sayed.mehrabalmomen.domain.model.companion.CompanionMood
import dev.sayed.mehrabalmomen.presentation.base.UiText

data class CompanionUiState(
    val mood: CompanionMood = CompanionMood.HAPPY,
    val dialogue: UiText? = null,
    val isVisible: Boolean = true,
    val isLaughing: Boolean = false,
    val isDoingTasbih: Boolean = false,
    val tapCount: Int = 0,
    val lastTapTime: Long = 0L,
    val lastInteractionTime: Long = 0L
)
