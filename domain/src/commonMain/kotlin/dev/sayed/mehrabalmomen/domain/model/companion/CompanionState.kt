package dev.sayed.mehrabalmomen.domain.model.companion

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Represents the current state of the spiritual companion rafiq.
 */
@OptIn(ExperimentalTime::class)
data class CompanionState(
    val lastInteractionMillis: Long = Clock.System.now().toEpochMilliseconds(),
    val quranReadToday: Boolean = false,
    val azkarReadToday: Boolean = false,
    val mood: CompanionMood = CompanionMood.HAPPY
)
