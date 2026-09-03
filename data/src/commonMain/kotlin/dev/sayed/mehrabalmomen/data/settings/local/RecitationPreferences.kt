package dev.sayed.mehrabalmomen.data.settings.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecitationPreferences(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val LAST_RECITER_ID = intPreferencesKey("last_reciter_id")
        private val LAST_RECITER_NAME_AR = stringPreferencesKey("last_reciter_name_ar")
        private val LAST_RECITER_NAME_EN = stringPreferencesKey("last_reciter_name_en")
        private val LAST_BASE_AUDIO_URL = stringPreferencesKey("last_base_audio_url")
        private val LAST_REWAYA_NAME = stringPreferencesKey("last_rewaya_name")
    }

    suspend fun saveLastReciter(
        id: Int,
        nameAr: String,
        nameEn: String,
        baseAudioUrl: String,
        rewayaName: String
    ) {
        dataStore.edit { preferences ->
            preferences[LAST_RECITER_ID] = id
            preferences[LAST_RECITER_NAME_AR] = nameAr
            preferences[LAST_RECITER_NAME_EN] = nameEn
            preferences[LAST_BASE_AUDIO_URL] = baseAudioUrl
            preferences[LAST_REWAYA_NAME] = rewayaName
        }
    }

    val lastReciter: Flow<LastReciter?> = dataStore.data.map { preferences ->
        val id = preferences[LAST_RECITER_ID] ?: return@map null
        LastReciter(
            id = id,
            nameAr = preferences[LAST_RECITER_NAME_AR] ?: "",
            nameEn = preferences[LAST_RECITER_NAME_EN] ?: "",
            baseAudioUrl = preferences[LAST_BASE_AUDIO_URL] ?: "",
            rewayaName = preferences[LAST_REWAYA_NAME] ?: ""
        )
    }

    data class LastReciter(
        val id: Int,
        val nameAr: String,
        val nameEn: String,
        val baseAudioUrl: String,
        val rewayaName: String
    )
}
