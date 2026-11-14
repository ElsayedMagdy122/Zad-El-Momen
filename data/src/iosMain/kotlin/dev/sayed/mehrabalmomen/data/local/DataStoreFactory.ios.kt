package dev.sayed.mehrabalmomen.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import platform.Foundation.NSHomeDirectory

actual class DataStoreFactory {
    actual fun create(): DataStore<Preferences> {
        val dbFilePath = NSHomeDirectory() + "/Documents/settings.preferences_pb"
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                dbFilePath.toPath()
            }
        )
    }
}
