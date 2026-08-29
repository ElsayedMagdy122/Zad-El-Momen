package dev.sayed.mehrabalmomen.domain.usecase

import dev.sayed.mehrabalmomen.domain.model.companion.CompanionMood
import dev.sayed.mehrabalmomen.domain.model.companion.CompanionState
import dev.sayed.mehrabalmomen.domain.repository.companion.CompanionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.ExperimentalTime

class ObserveCompanionUseCase(
    private val companionRepository: CompanionRepository
) {

    @OptIn(ExperimentalTime::class)
    operator fun invoke(): Flow<CompanionState> = companionRepository.observeCompanionState().map { state ->
        val mood = calculateMood(state)
        state.copy(mood = mood)
    }

    private fun calculateMood(state: CompanionState): CompanionMood {
        val lastInteraction = state.lastInteractionMillis
        val diffDays = (System.currentTimeMillis() - lastInteraction) / (1000 * 60 * 60 * 24)

        return when {
            diffDays >= 2 -> CompanionMood.SAD
            state.quranReadToday && state.azkarReadToday -> CompanionMood.EXCITED
            state.quranReadToday || state.azkarReadToday -> CompanionMood.HAPPY
            else -> CompanionMood.THINKING
        }
    }
}
