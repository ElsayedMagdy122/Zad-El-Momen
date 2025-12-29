package dev.sayed.mehrabalmomen.data.local.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.sayed.mehrabalmomen.data.local.SettingsKeys
import dev.sayed.mehrabalmomen.domain.entity.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.entity.Madhab
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
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

    override fun observeMadhab(): Flow<Madhab> =
        dataStore.data.map {
            Madhab.valueOf(
                it[SettingsKeys.MADHAB] ?: Madhab.SHAFI.name
            )
        }

    override fun observeCalculationMethod(): Flow<CalculationMethod> =
        dataStore.data.map {
            CalculationMethod.valueOf(
                it[SettingsKeys.CALCULATION]
                    ?: CalculationMethod.MUSLIM_WORLD_LEAGUE.name
            )
        }

    override fun observeLocation(): Flow<Location> =
        dataStore.data.map { prefs ->
            Location(
                latitude = prefs[SettingsKeys.LATITUDE_KEY] ?: 0.0,
                longitude = prefs[SettingsKeys.LONGITUDE_KEY] ?: 0.0
            )
        }

    override fun observeOnboardingComplete(): Flow<Boolean> =
        dataStore.data.map { it[SettingsKeys.ONBOARDING_COMPLETE] ?: false }

    override fun observeLanguage(): Flow<AppSettings.Language> =
        dataStore.data.map {
            val value = it[SettingsKeys.LANGUAGE]
            if (value != null) AppSettings.Language.valueOf(value) else AppSettings.Language.ARABIC
        }

    override fun observeTheme(): Flow<AppSettings.Theme> =
        dataStore.data.map {
            val value = it[SettingsKeys.THEME]
            if (value != null) AppSettings.Theme.valueOf(value) else AppSettings.Theme.SYSTEM
        }


    override fun observeAppSettings(): Flow<AppSettings> =
        dataStore.data.map { prefs ->
            AppSettings(
                madhab = Madhab.valueOf(
                    prefs[SettingsKeys.MADHAB] ?: Madhab.SHAFI.name
                ),
                calculationMethod = CalculationMethod.valueOf(
                    prefs[SettingsKeys.CALCULATION]
                        ?: CalculationMethod.MUSLIM_WORLD_LEAGUE.name
                ),
                latitude = prefs[SettingsKeys.LATITUDE_KEY] ?: 0.0,
                longitude = prefs[SettingsKeys.LONGITUDE_KEY] ?: 0.0,
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