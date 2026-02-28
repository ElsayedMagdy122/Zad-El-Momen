package dev.sayed.mehrabalmomen.data.local.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import dev.sayed.mehrabalmomen.domain.entity.quran.ReadingProgress
import dev.sayed.mehrabalmomen.domain.repository.quran.ReadingProgressRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ReadingProgressRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : ReadingProgressRepository {

    override suspend fun save(surahId: Int, ayahId: Int) {
        dataStore.edit {
            it[ContinueTilawahKeys.SURAH_ID] = surahId
            it[ContinueTilawahKeys.AYAH_ID] = ayahId
        }
    }

    override fun observe(): Flow<ReadingProgress?> =
        dataStore.data.map { prefs ->
            val surahId = prefs[ContinueTilawahKeys.SURAH_ID]
            val ayahId = prefs[ContinueTilawahKeys.AYAH_ID]

            if (surahId != null && ayahId != null) {
                ReadingProgress(surahId, ayahId)
            } else  ReadingProgress(1, 1)
        }

    object ContinueTilawahKeys {
        val SURAH_ID = intPreferencesKey("continue_surah_id")
        val AYAH_ID = intPreferencesKey("continue_ayah_id")
    }
}