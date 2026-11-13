package dev.sayed.mehrabalmomen.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import platform.Foundation.NSHomeDirectory

actual class DataStoreFactory {
    actual fun create(name: String): DataStore<Preferences> {
        val fileName = if (name.endsWith(".preferences_pb")) name else "$name.preferences_pb"
        val path = NSHomeDirectory() + "/Documents/$fileName"
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                path.toPath()
            }
        )
    }
}
