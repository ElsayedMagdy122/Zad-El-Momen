package dev.sayed.mehrabalmomen.domain.usecase

import dev.sayed.mehrabalmomen.domain.model.companion.CompanionMood
import dev.sayed.mehrabalmomen.domain.model.companion.CompanionState

class GetCompanionMessageUseCase {

    fun execute(
        state: CompanionState,
        hour: Int,
        isManualInteraction: Boolean = false
    ): CompanionMessage {
        if (isManualInteraction) {
            return listOf(
                CompanionMessage.INTERACT_1,
                CompanionMessage.INTERACT_2,
                CompanionMessage.INTERACT_3,
                CompanionMessage.INTERACT_4,
                CompanionMessage.INTERACT_5
            ).random()
        }

        if (hour in 5..9 && !state.azkarReadToday) {
            return listOf(CompanionMessage.MORNING_1, CompanionMessage.MORNING_2).random()
        }
        if (hour in 18..22 && !state.azkarReadToday) {
            return listOf(CompanionMessage.EVENING_1, CompanionMessage.EVENING_2).random()
        }

        return when (state.mood) {
            CompanionMood.HAPPY -> listOf(
                CompanionMessage.HAPPY_1,
                CompanionMessage.HAPPY_2,
                CompanionMessage.HAPPY_3,
                CompanionMessage.HAPPY_4,
                CompanionMessage.HAPPY_5,
                CompanionMessage.HAPPY_6
            ).random()

            CompanionMood.SAD -> listOf(
                CompanionMessage.SAD_1,
                CompanionMessage.SAD_2,
                CompanionMessage.SAD_3,
                CompanionMessage.SAD_4,
                CompanionMessage.SAD_5
            ).random()

            CompanionMood.EXCITED -> listOf(
                CompanionMessage.EXCITED_1,
                CompanionMessage.EXCITED_2,
                CompanionMessage.EXCITED_3,
                CompanionMessage.EXCITED_4,
                CompanionMessage.EXCITED_5
            ).random()

            CompanionMood.THINKING -> {
                val pool = mutableListOf<CompanionMessage>()
                if (!state.quranReadToday) pool.add(CompanionMessage.THINKING_QURAN)
                if (!state.azkarReadToday) pool.add(CompanionMessage.THINKING_AZKAR)

                pool.addAll(listOf(
                    CompanionMessage.THINKING_GENERAL_1,
                    CompanionMessage.THINKING_GENERAL_2,
                    CompanionMessage.THINKING_GENERAL_3,
                    CompanionMessage.THINKING_GENERAL_4,
                    CompanionMessage.THINKING_GENERAL_5
                ))
                pool.random()
            }

            CompanionMood.SLEEPING -> CompanionMessage.SLEEPING
        }
    }

    enum class CompanionMessage {
        INTERACT_1, INTERACT_2, INTERACT_3, INTERACT_4, INTERACT_5,
        MORNING_1, MORNING_2,
        EVENING_1, EVENING_2,
        HAPPY_1, HAPPY_2, HAPPY_3, HAPPY_4, HAPPY_5, HAPPY_6,
        SAD_1, SAD_2, SAD_3, SAD_4, SAD_5,
        EXCITED_1, EXCITED_2, EXCITED_3, EXCITED_4, EXCITED_5,
        THINKING_QURAN, THINKING_AZKAR,
        THINKING_GENERAL_1, THINKING_GENERAL_2, THINKING_GENERAL_3, THINKING_GENERAL_4, THINKING_GENERAL_5,
        SLEEPING,
        TICKLE,
        TASBIH_1, TASBIH_2, TASBIH_3, TASBIH_4
    }
}
