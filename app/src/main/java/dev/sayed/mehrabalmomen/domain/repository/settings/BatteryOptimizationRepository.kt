package dev.sayed.mehrabalmomen.domain.repository.settings

interface BatteryOptimizationRepository {
    fun getBrandInstructions(
        manufacturer: String,
        isArabic: Boolean
    ): List<String>
}