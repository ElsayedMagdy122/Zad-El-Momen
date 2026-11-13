package dev.sayed.mehrabalmomen.presentation.screen.companion

import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.domain.usecase.GetCompanionMessageUseCase.CompanionMessage
import dev.sayed.mehrabalmomen.presentation.base.UiText

/**
 * Maps domain-level companion messages to platform-independent [UiText].
 */
object CompanionDialogue {

    fun getMessage(message: CompanionMessage): UiText {
        val res = when (message) {
            CompanionMessage.INTERACT_1 -> Res.string.rafiq_interact_1
            CompanionMessage.INTERACT_2 -> Res.string.rafiq_interact_2
            CompanionMessage.INTERACT_3 -> Res.string.rafiq_interact_3
            CompanionMessage.INTERACT_4 -> Res.string.rafiq_interact_4
            CompanionMessage.INTERACT_5 -> Res.string.rafiq_interact_5
            
            CompanionMessage.MORNING_1 -> Res.string.rafiq_morning_1
            CompanionMessage.MORNING_2 -> Res.string.rafiq_morning_2
            
            CompanionMessage.EVENING_1 -> Res.string.rafiq_evening_1
            CompanionMessage.EVENING_2 -> Res.string.rafiq_evening_2
            
            CompanionMessage.HAPPY_1 -> Res.string.rafiq_happy_1
            CompanionMessage.HAPPY_2 -> Res.string.rafiq_happy_2
            CompanionMessage.HAPPY_3 -> Res.string.rafiq_happy_3
            CompanionMessage.HAPPY_4 -> Res.string.rafiq_happy_4
            CompanionMessage.HAPPY_5 -> Res.string.rafiq_happy_5
            CompanionMessage.HAPPY_6 -> Res.string.rafiq_happy_6
            
            CompanionMessage.SAD_1 -> Res.string.rafiq_sad_1
            CompanionMessage.SAD_2 -> Res.string.rafiq_sad_2
            CompanionMessage.SAD_3 -> Res.string.rafiq_sad_3
            CompanionMessage.SAD_4 -> Res.string.rafiq_sad_4
            CompanionMessage.SAD_5 -> Res.string.rafiq_sad_5
            
            CompanionMessage.EXCITED_1 -> Res.string.rafiq_excited_1
            CompanionMessage.EXCITED_2 -> Res.string.rafiq_excited_2
            CompanionMessage.EXCITED_3 -> Res.string.rafiq_excited_3
            CompanionMessage.EXCITED_4 -> Res.string.rafiq_excited_4
            CompanionMessage.EXCITED_5 -> Res.string.rafiq_excited_5
            
            CompanionMessage.THINKING_QURAN -> Res.string.rafiq_thinking_quran
            CompanionMessage.THINKING_AZKAR -> Res.string.rafiq_thinking_azkar
            
            CompanionMessage.THINKING_GENERAL_1 -> Res.string.rafiq_thinking_general_1
            CompanionMessage.THINKING_GENERAL_2 -> Res.string.rafiq_thinking_general_2
            CompanionMessage.THINKING_GENERAL_3 -> Res.string.rafiq_thinking_general_3
            CompanionMessage.THINKING_GENERAL_4 -> Res.string.rafiq_thinking_general_4
            CompanionMessage.THINKING_GENERAL_5 -> Res.string.rafiq_thinking_general_5
            
            CompanionMessage.SLEEPING -> Res.string.azkar_sleep
            CompanionMessage.TICKLE -> Res.string.rafiq_tickle
            
            CompanionMessage.TASBIH_1 -> Res.string.rafiq_tasbih_1
            CompanionMessage.TASBIH_2 -> Res.string.rafiq_tasbih_2
            CompanionMessage.TASBIH_3 -> Res.string.rafiq_tasbih_3
            CompanionMessage.TASBIH_4 -> Res.string.rafiq_tasbih_4
        }
        return UiText.Resource(res)
    }
}
