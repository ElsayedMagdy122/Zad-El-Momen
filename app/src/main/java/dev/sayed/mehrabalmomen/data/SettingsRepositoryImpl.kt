package dev.sayed.mehrabalmomen.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.sayed.mehrabalmomen.domain.AppSettings
import dev.sayed.mehrabalmomen.domain.entity.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.entity.Madhab
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {
    override suspend fun saveMadhab(madhab: Madhab) {
        dataStore.edit { it[DataStoreKeys.MADHAB] = madhab.name }
    }

    override suspend fun saveCalculationMethod(method: CalculationMethod) {
        dataStore.edit { it[DataStoreKeys.CALCULATION] = method.name }
    }

    override suspend fun saveLocation(lat: Double, lng: Double) {
        dataStore.edit {
            it[DataStoreKeys.LATITUDE_KEY] = lat
            it[DataStoreKeys.LONGITUDE_KEY] = lng
        }
    }

    override fun observeMadhab(): Flow<Madhab> =
        dataStore.data.map {
            Madhab.valueOf(
                it[DataStoreKeys.MADHAB] ?: Madhab.SHAFI.name
            )
        }

    override fun observeCalculationMethod(): Flow<CalculationMethod> =
        dataStore.data.map {
            CalculationMethod.valueOf(
                it[DataStoreKeys.CALCULATION]
                    ?: CalculationMethod.MUSLIM_WORLD_LEAGUE.name
            )
        }

    override fun observeLocation(): Flow<Location> =
        dataStore.data.map {prefs->
            Location(
                latitude = prefs[DataStoreKeys.LATITUDE_KEY] ?: 0.0,
                longitude = prefs[DataStoreKeys.LONGITUDE_KEY] ?: 0.0
            )
        }

    override fun observeAll(): Flow<AppSettings> =
        dataStore.data.map { prefs ->
            AppSettings(
                madhab = Madhab.valueOf(
                    prefs[DataStoreKeys.MADHAB] ?: Madhab.SHAFI.name
                ),
                calculationMethod = CalculationMethod.valueOf(
                    prefs[DataStoreKeys.CALCULATION]
                        ?: CalculationMethod.MUSLIM_WORLD_LEAGUE.name
                ),
                latitude = prefs[DataStoreKeys.LATITUDE_KEY] ?: 0.0,
                longitude = prefs[DataStoreKeys.LONGITUDE_KEY] ?: 0.0
            )
        }
}