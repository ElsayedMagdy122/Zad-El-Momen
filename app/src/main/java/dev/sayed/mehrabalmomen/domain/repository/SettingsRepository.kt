package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.entity.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.entity.Madhab
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun saveMadhab(madhab: Madhab)
    suspend fun saveCalculationMethod(method: CalculationMethod)
    suspend fun saveLocation(location: Location)
    suspend fun setOnboardingComplete()
    fun observeMadhab(): Flow<Madhab>
    fun observeCalculationMethod(): Flow<CalculationMethod>
    fun observeLocation(): Flow<Location>
    fun observeOnboardingComplete(): Flow<Boolean>
    fun observeAppSettings(): Flow<AppSettings>
}