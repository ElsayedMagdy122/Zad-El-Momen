package dev.sayed.mehrabalmomen.presentation.screen.companion

import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.model.companion.CompanionMood
import dev.sayed.mehrabalmomen.domain.model.companion.CompanionState
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object CompanionDialogue {

    @OptIn(ExperimentalTime::class)
    fun getMessage(state: CompanionState, isManualInteraction: Boolean = false): Int {
        val hour = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).hour
        
        if (isManualInteraction) {
            return listOf(
                R.string.rafiq_interact_1,
                R.string.rafiq_interact_2,
                R.string.rafiq_interact_3,
                R.string.rafiq_interact_4,
                R.string.rafiq_interact_5
            ).random()
        }

        // Time-based priority
        if (hour in 5..9 && !state.azkarReadToday) return listOf(R.string.rafiq_morning_1, R.string.rafiq_morning_2).random()
        if (hour in 18..22 && !state.azkarReadToday) return listOf(R.string.rafiq_evening_1, R.string.rafiq_evening_2).random()

        return when (state.mood) {
            CompanionMood.HAPPY -> listOf(
                R.string.rafiq_happy_1,
                R.string.rafiq_happy_2,
                R.string.rafiq_happy_3,
                R.string.rafiq_happy_4,
                R.string.rafiq_happy_5,
                R.string.rafiq_happy_6
            ).random()
            
            CompanionMood.SAD -> listOf(
                R.string.rafiq_sad_1,
                R.string.rafiq_sad_2,
                R.string.rafiq_sad_3,
                R.string.rafiq_sad_4,
                R.string.rafiq_sad_5
            ).random()
            
            CompanionMood.EXCITED -> listOf(
                R.string.rafiq_excited_1,
                R.string.rafiq_excited_2,
                R.string.rafiq_excited_3,
                R.string.rafiq_excited_4,
                R.string.rafiq_excited_5
            ).random()
            
            CompanionMood.THINKING -> {
                val pool = mutableListOf<Int>()
                if (!state.quranReadToday) pool.add(R.string.rafiq_thinking_quran)
                if (!state.azkarReadToday) pool.add(R.string.rafiq_thinking_azkar)
                
                pool.addAll(listOf(
                    R.string.rafiq_thinking_general_1,
                    R.string.rafiq_thinking_general_2,
                    R.string.rafiq_thinking_general_3,
                    R.string.rafiq_thinking_general_4,
                    R.string.rafiq_thinking_general_5
                ))
                pool.random()
            }
            
            CompanionMood.SLEEPING -> R.string.azkar_sleep
        }
    }
}
