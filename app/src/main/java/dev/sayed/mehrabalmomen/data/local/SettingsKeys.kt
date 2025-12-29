package dev.sayed.mehrabalmomen.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.sayed.mehrabalmomen.domain.entity.Prayer

object SettingsKeys {
    val LATITUDE_KEY = doublePreferencesKey("latitude")
    val LONGITUDE_KEY = doublePreferencesKey("longitude")
    val MADHAB = stringPreferencesKey("madhab")
    val LANGUAGE = stringPreferencesKey("language")
    val THEME =  stringPreferencesKey("theme")
    val CALCULATION = stringPreferencesKey("calculation")
    val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    val ALARMS_SCHEDULED = booleanPreferencesKey("alarms_scheduled")
    fun prayerKey(prayer: Prayer.PrayerName) =
        booleanPreferencesKey("prayer_enabled_${prayer.name}")
}