package dev.sayed.mehrabalmomen.domain.model.companion

data class CompanionState(
    val lastInteractionMillis: Long = System.currentTimeMillis(),
    val quranReadToday: Boolean = false,
    val azkarReadToday: Boolean = false,
    val mood: CompanionMood = CompanionMood.HAPPY
)
