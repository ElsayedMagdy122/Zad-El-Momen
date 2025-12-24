package dev.sayed.mehrabalmomen.data.local.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.sayed.mehrabalmomen.data.local.SettingsKeys
import dev.sayed.mehrabalmomen.domain.entity.Prayer
import dev.sayed.mehrabalmomen.domain.repository.PrayerNotificationsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PrayerNotificationsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : PrayerNotificationsRepository {

    override suspend fun setPrayerEnabled(
        prayer: Prayer.PrayerName,
        enabled: Boolean
    ) {
        dataStore.edit {
            it[SettingsKeys.prayerKey(prayer)] = enabled
        }
    }

    override fun observePrayerEnabled(
        prayer: Prayer.PrayerName
    ): Flow<Boolean> =
        dataStore.data.map {
            it[SettingsKeys.prayerKey(prayer)] ?: false
        }

    override fun observeAll(): Flow<Map<Prayer.PrayerName, Boolean>> =
        dataStore.data.map { prefs ->
            val map = Prayer.PrayerName.entries.associateWith { prayer ->
                prefs[SettingsKeys.prayerKey(prayer)] ?: false
            }
            map
        }
}