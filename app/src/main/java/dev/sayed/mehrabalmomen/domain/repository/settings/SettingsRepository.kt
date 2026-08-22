package dev.sayed.mehrabalmomen.domain.repository.settings

import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerSettings
import dev.sayed.mehrabalmomen.domain.entity.widget.prayer.PrayerWidgetSettings
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun saveMadhab(madhab: Madhab)
    suspend fun saveCalculationMethod(method: CalculationMethod)

    /**
     * Persists the user's selected calculation location.
     *
     * @param location coordinates and descriptive address fields to save.
     */
    suspend fun saveLocation(location: Location)

    suspend fun saveLanguage(language: AppSettings.Language)
    suspend fun saveTheme(theme: AppSettings.Theme)
    suspend fun setOnboardingComplete()
    fun observeLocation(): Flow<Location>
    fun observeOnboardingComplete(): Flow<Boolean>

    /**
     * Observes the prayer calculation preferences.
     *
     * @return a flow of current Madhab, calculation method, and location values.
     */
    fun observePrayerSettings(): Flow<PrayerSettings>

    /**
     * Observes every saved preference required to generate a prayer widget snapshot.
     *
     * @return a flow of calculation settings, widget language, and location setup state.
     */
    fun observePrayerWidgetSettings(): Flow<PrayerWidgetSettings>
    suspend fun saveQuranFontSize(size: Int)
    fun observeQuranFontSize(): Flow<Int>
    suspend fun saveSelectedMoazen(fileName: String)
    fun observeSelectedMoazen(): Flow<String>
    suspend fun saveTafseer(type: String)
    fun observeTafseer(): Flow<String>

    /**
     * Observes the complete application settings model.
     *
     * @return a flow containing prayer, alarm, theme, and language settings.
     */
    fun observeAppSettings(): Flow<AppSettings>
}
