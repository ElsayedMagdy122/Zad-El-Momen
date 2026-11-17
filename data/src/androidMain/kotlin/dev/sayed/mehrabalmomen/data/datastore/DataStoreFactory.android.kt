package dev.sayed.mehrabalmomen.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

fun createAndroidDataStore(context: Context, fileName: String = "mehrab_settings.preferences_pb"): DataStore<Preferences> {
    return createDataStore {
        context.filesDir.resolve(fileName).absolutePath
    }
}
