package dev.sayed.mehrabalmomen.presentation.screen.companion

import androidx.compose.runtime.Immutable
import dev.sayed.mehrabalmomen.domain.model.companion.CompanionMood

@Immutable
data class CompanionUiState(
    val mood: CompanionMood = CompanionMood.HAPPY,
    val dialogueRes: Int? = null,
    val isVisible: Boolean = true,
    val isLaughing: Boolean = false,
    val isDoingTasbih: Boolean = false,

    val lastInteractionTime: Long = System.currentTimeMillis(),
    val tapCount: Int = 0,
    val lastTapTime: Long = 0L
)
