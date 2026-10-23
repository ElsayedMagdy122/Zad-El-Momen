package dev.sayed.mehrabalmomen.domain.entity.azkar

data class AzkarStats(
    val streak: Int,
    val completedCountToday: Int,
    val totalAzkarCount: Int,
    val remainingCount: Int,
    val completionPercentage: Int
)
