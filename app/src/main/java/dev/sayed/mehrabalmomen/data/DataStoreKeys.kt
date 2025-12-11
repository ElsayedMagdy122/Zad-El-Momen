package dev.sayed.mehrabalmomen.data

import androidx.datastore.preferences.core.doublePreferencesKey


object DataStoreKeys {
    val LATITUDE_KEY = doublePreferencesKey("latitude")
    val LONGITUDE_KEY = doublePreferencesKey("longitude")
}