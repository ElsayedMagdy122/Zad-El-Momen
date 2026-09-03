package dev.sayed.mehrabalmomen.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

expect fun createDataStore(fileName: String): DataStore<Preferences>
