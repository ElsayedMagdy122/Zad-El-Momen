package dev.sayed.mehrabalmomen.data.reminders

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import dev.sayed.mehrabalmomen.domain.model.ReminderConfig
import dev.sayed.mehrabalmomen.domain.model.ReminderType
import dev.sayed.mehrabalmomen.domain.repository.reminders.ReminderSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReminderSettingsRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : ReminderSettingsRepository {

    override fun observeAllReminders(): Flow<List<ReminderConfig>> = dataStore.data.map { prefs ->
        ReminderType.entries.map { type ->
            ReminderConfig(
                type = type,
                isEnabled = prefs[booleanPreferencesKey("${type.preferenceKey}_enabled")] ?: true,
                hour = prefs[intPreferencesKey("${type.preferenceKey}_hour")] ?: type.defaultHour,
                minute = prefs[intPreferencesKey("${type.preferenceKey}_minute")]
                    ?: type.defaultMinute
            )
        }
    }

    override suspend fun saveReminderConfig(
        type: ReminderType,
        isEnabled: Boolean,
        hour: Int,
        minute: Int
    ) {
        dataStore.edit { prefs ->
            prefs[booleanPreferencesKey("${type.preferenceKey}_enabled")] = isEnabled
            prefs[intPreferencesKey("${type.preferenceKey}_hour")] = hour
            prefs[intPreferencesKey("${type.preferenceKey}_minute")] = minute
        }
    }
}