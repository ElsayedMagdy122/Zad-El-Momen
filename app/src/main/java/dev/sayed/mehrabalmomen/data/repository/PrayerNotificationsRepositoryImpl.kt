package dev.sayed.mehrabalmomen.data.repository

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.sayed.mehrabalmomen.data.DataStoreKeys
import dev.sayed.mehrabalmomen.domain.entity.Prayer
import dev.sayed.mehrabalmomen.domain.repository.PrayerNotificationsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map

class PrayerNotificationsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : PrayerNotificationsRepository {
    private val _events = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val events = _events.asSharedFlow()
    override suspend fun setPrayerEnabled(
        prayer: Prayer.PrayerName,
        enabled: Boolean
    ) {
        dataStore.edit {
            it[DataStoreKeys.prayerKey(prayer)] = enabled
        }
        _events.tryEmit(Unit)
    }

    override fun observePrayerEnabled(
        prayer: Prayer.PrayerName
    ): Flow<Boolean> =
        dataStore.data.map {
            it[DataStoreKeys.prayerKey(prayer)] ?: false
        }

    override fun observeAll(): Flow<Map<Prayer.PrayerName, Boolean>> =
        dataStore.data.map { prefs ->
            val map = Prayer.PrayerName.entries.associateWith { prayer ->
                prefs[DataStoreKeys.prayerKey(prayer)] ?: false
            }
            map
        }
}