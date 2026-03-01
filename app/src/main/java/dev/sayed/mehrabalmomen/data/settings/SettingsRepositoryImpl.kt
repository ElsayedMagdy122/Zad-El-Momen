package dev.sayed.mehrabalmomen.data.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.entity.prayer.PrayerSettings
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    override suspend fun saveMadhab(madhab: Madhab) {
        dataStore.edit { it[SettingsKeys.MADHAB] = madhab.name }
    }

    override suspend fun saveCalculationMethod(method: CalculationMethod) {
        dataStore.edit { it[SettingsKeys.CALCULATION] = method.name }
    }

    override suspend fun saveLocation(location: Location) {
        dataStore.edit {
            it[SettingsKeys.LATITUDE_KEY] = location.latitude
            it[SettingsKeys.LONGITUDE_KEY] = location.longitude
            it[SettingsKeys.COUNTRY_KEY] = location.country
            it[SettingsKeys.STATE_KEY] = location.state
        }
    }

    override suspend fun saveLanguage(language: AppSettings.Language) {
        dataStore.edit { it[SettingsKeys.LANGUAGE] = language.name }
    }

    override suspend fun saveTheme(theme: AppSettings.Theme) {
        dataStore.edit { it[SettingsKeys.THEME] = theme.name }
    }

    override suspend fun setOnboardingComplete() {
        dataStore.edit { it[SettingsKeys.ONBOARDING_COMPLETE] = true }
    }

    override fun observeLocation(): Flow<Location> =
        dataStore.data.map { prefs ->
            Location(
                latitude = prefs[SettingsKeys.LATITUDE_KEY] ?: 0.0,
                longitude = prefs[SettingsKeys.LONGITUDE_KEY] ?: 0.0,
                country = prefs[SettingsKeys.COUNTRY_KEY] ?: "Unknown",
                state = prefs[SettingsKeys.STATE_KEY] ?: "Unknown"
            )
        }

    override fun observeOnboardingComplete(): Flow<Boolean> =
        dataStore.data.map { it[SettingsKeys.ONBOARDING_COMPLETE] ?: false }

    override fun observePrayerSettings(): Flow<PrayerSettings> =
        dataStore.data.map { prefs ->
            PrayerSettings(
                madhab = Madhab.valueOf(
                    prefs[SettingsKeys.MADHAB] ?: Madhab.SHAFI.name
                ),
                calculationMethod = CalculationMethod.valueOf(
                    prefs[SettingsKeys.CALCULATION]
                        ?: CalculationMethod.EGYPTIAN.name
                ),
                location = Location(
                    latitude = prefs[SettingsKeys.LATITUDE_KEY] ?: 0.0,
                    longitude = prefs[SettingsKeys.LONGITUDE_KEY] ?: 0.0,
                    country = prefs[SettingsKeys.COUNTRY_KEY] ?: "Unknown",
                    state = prefs[SettingsKeys.STATE_KEY] ?: "Unknown"
                )
            )
        }

    override fun observeAppSettings(): Flow<AppSettings> =
        dataStore.data.map { prefs ->
            AppSettings(
                prayerSettings = PrayerSettings(
                    madhab = Madhab.valueOf(
                        prefs[SettingsKeys.MADHAB] ?: Madhab.SHAFI.name
                    ),
                    calculationMethod = CalculationMethod.valueOf(
                        prefs[SettingsKeys.CALCULATION]
                            ?: CalculationMethod.MUSLIM_WORLD_LEAGUE.name
                    ),
                    location = Location(
                        latitude = prefs[SettingsKeys.LATITUDE_KEY] ?: 0.0,
                        longitude = prefs[SettingsKeys.LONGITUDE_KEY] ?: 0.0,
                        country = prefs[SettingsKeys.COUNTRY_KEY] ?: "Unknown",
                        state = prefs[SettingsKeys.STATE_KEY] ?: "Unknown"
                    )
                ),

                alarmsScheduled = prefs[SettingsKeys.ALARMS_SCHEDULED] ?: false,
                theme = AppSettings.Theme.valueOf(
                    prefs[SettingsKeys.THEME] ?: AppSettings.Theme.SYSTEM.name
                ),
                language = AppSettings.Language.valueOf(
                    prefs[SettingsKeys.LANGUAGE] ?: AppSettings.Language.ARABIC.name
                )
            )
        }
}