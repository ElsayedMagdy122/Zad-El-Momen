package dev.sayed.mehrabalmomen.domain.repository.settings

import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerSettings
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun saveMadhab(madhab: Madhab)
    suspend fun saveCalculationMethod(method: CalculationMethod)
    suspend fun saveLocation(location: Location)

    suspend fun saveLanguage(language: AppSettings.Language)
    suspend fun saveTheme(theme: AppSettings.Theme)
    suspend fun setOnboardingComplete()
    fun observeLocation(): Flow<Location>
    fun observeOnboardingComplete(): Flow<Boolean>
    fun observePrayerSettings(): Flow<PrayerSettings>
    suspend fun saveQuranFontSize(size: Int)
    fun observeQuranFontSize(): Flow<Int>
    suspend fun saveSelectedMoazen(fileName: String)
    fun observeSelectedMoazen(): Flow<String>
    suspend fun saveTafseer(type: String)
    fun observeTafseer(): Flow<String>
    fun observeAppSettings(): Flow<AppSettings>
}