package dev.sayed.mehrabalmomen.domain.usecase

import dev.sayed.mehrabalmomen.domain.model.companion.CompanionMood
import dev.sayed.mehrabalmomen.domain.model.companion.CompanionState
import dev.sayed.mehrabalmomen.domain.repository.companion.CompanionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class ObserveCompanionUseCase(
    private val repository: CompanionRepository
) {

    @OptIn(ExperimentalTime::class)
    operator fun invoke(): Flow<Pair<CompanionState, CompanionMood>> {
        return repository.observeCompanionState().map { state ->
            val mood = calculateMood(state.lastInteractionMillis, state.quranReadToday, state.azkarReadToday)
            state to mood
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun calculateMood(lastInteraction: Long, quranRead: Boolean, azkarRead: Boolean): CompanionMood {
        val now = Clock.System.now().toEpochMilliseconds()
        val diffDays = (now - lastInteraction) / (1000 * 60 * 60 * 24)

        return when {
            diffDays >= 3 -> CompanionMood.SAD
            quranRead && azkarRead -> CompanionMood.EXCITED
            quranRead || azkarRead -> CompanionMood.HAPPY
            else -> CompanionMood.THINKING
        }
    }
}
