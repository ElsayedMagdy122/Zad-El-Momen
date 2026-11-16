package dev.sayed.mehrabalmomen.data.settings.repositiory

import dev.sayed.mehrabalmomen.domain.repository.settings.BatteryOptimizationRepository

class IosBatteryOptimizationRepositoryImpl : BatteryOptimizationRepository {
    override fun isIgnoringBatteryOptimizations(): Boolean = true
    override fun getBrandInstructions(manufacturer: String, isArabic: Boolean): List<String> = emptyList()
}
