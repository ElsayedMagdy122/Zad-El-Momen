package dev.sayed.mehrabalmomen.data.companion

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dev.sayed.mehrabalmomen.data.settings.SettingsKeys
import dev.sayed.mehrabalmomen.domain.model.companion.CompanionState
import dev.sayed.mehrabalmomen.domain.repository.companion.CompanionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CompanionRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : CompanionRepository {

    @OptIn(ExperimentalTime::class)
    override fun observeCompanionState(): Flow<CompanionState> = dataStore.data.map { prefs ->
        val lastResetDate = prefs[SettingsKeys.LAST_TASK_RESET_DATE] ?: ""
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()

        val isSameDay = lastResetDate == today

        CompanionState(
            lastInteractionMillis = prefs[SettingsKeys.LAST_INTERACTION_MILLIS] ?: Clock.System.now().toEpochMilliseconds(),
            quranReadToday = if (isSameDay) (prefs[SettingsKeys.QURAN_READ_TODAY] ?: false) else false,
            azkarReadToday = if (isSameDay) (prefs[SettingsKeys.AZKAR_READ_TODAY] ?: false) else false
        )
    }

    override fun observeCompanionEnabled(): Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[SettingsKeys.COMPANION_ENABLED] ?: true
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun updateQuranReadStatus(read: Boolean) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        dataStore.edit {
            it[SettingsKeys.QURAN_READ_TODAY] = read
            it[SettingsKeys.LAST_TASK_RESET_DATE] = today
        }
    }

    @OptIn(ExperimentalTime::class)
    override suspend fun updateAzkarReadStatus(read: Boolean) {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        dataStore.edit {
            it[SettingsKeys.AZKAR_READ_TODAY] = read
            it[SettingsKeys.LAST_TASK_RESET_DATE] = today
        }
    }

    override suspend fun updateLastInteraction(millis: Long) {
        dataStore.edit {
            it[SettingsKeys.LAST_INTERACTION_MILLIS] = millis
        }
    }

    override suspend fun updateCompanionEnabled(enabled: Boolean) {
        dataStore.edit {
            it[SettingsKeys.COMPANION_ENABLED] = enabled
        }
    }
}
