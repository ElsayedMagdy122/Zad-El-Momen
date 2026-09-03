package dev.sayed.mehrabalmomen.domain.model.companion

/**
 * Represents the current state of the spiritual companion rafiq.
 */
data class CompanionState(
    val lastInteractionMillis: Long = kotlinx.datetime.Clock.System.now().toEpochMilliseconds(),
    val quranReadToday: Boolean = false,
    val azkarReadToday: Boolean = false,
    val mood: CompanionMood = CompanionMood.HAPPY
)
