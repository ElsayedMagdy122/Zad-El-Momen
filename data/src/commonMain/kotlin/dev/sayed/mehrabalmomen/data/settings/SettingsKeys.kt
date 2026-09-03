package dev.sayed.mehrabalmomen.data.settings

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer

object SettingsKeys {
    val LATITUDE_KEY = doublePreferencesKey("latitude")
    val LONGITUDE_KEY = doublePreferencesKey("longitude")
    val COUNTRY_KEY = stringPreferencesKey("country")
    val STATE_KEY = stringPreferencesKey("state")
    val MADHAB = stringPreferencesKey("madhab")
    val LANGUAGE = stringPreferencesKey("language")
    val THEME = stringPreferencesKey("theme")
    val CALCULATION = stringPreferencesKey("calculation")
    val QURAN_FONT_SIZE = intPreferencesKey("quran_font_size")
    val TAFSEER_TYPE = stringPreferencesKey("tafseer_type")
    val SELECTED_MOAZEN = stringPreferencesKey("selected_moazen")
    val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    val ALARMS_SCHEDULED = booleanPreferencesKey("alarms_scheduled")

    // Companion Keys
    val LAST_INTERACTION_MILLIS = longPreferencesKey("companion_last_interaction")
    val QURAN_READ_TODAY = booleanPreferencesKey("companion_quran_read")
    val AZKAR_READ_TODAY = booleanPreferencesKey("companion_azkar_read")
    val LAST_TASK_RESET_DATE = stringPreferencesKey("companion_last_reset_date")
    val COMPANION_ENABLED = booleanPreferencesKey("companion_enabled")

    fun prayerKey(prayer: Prayer.PrayerName) =
        booleanPreferencesKey("prayer_enabled_${prayer.name}")
}
