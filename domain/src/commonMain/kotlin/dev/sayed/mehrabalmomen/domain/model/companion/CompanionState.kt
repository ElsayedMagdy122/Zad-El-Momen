package dev.sayed.mehrabalmomen.domain.model.companion

import kotlinx.datetime.Clock as DateClock

/**
 * Represents the current state of the spiritual companion rafiq.
 */
data class CompanionState(
    // TODO: Fix Clock.System.now() resolution issues in KMP
    val lastInteractionMillis: Long = 0L,
    val quranReadToday: Boolean = false,
    val azkarReadToday: Boolean = false,
    val mood: CompanionMood = CompanionMood.HAPPY
)
