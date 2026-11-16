package dev.sayed.mehrabalmomen.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import platform.Foundation.NSHomeDirectory

fun createIosDataStore(fileName: String = "mehrab_settings.preferences_pb"): DataStore<Preferences> {
    val dbFilePath = NSHomeDirectory() + "/Documents/$fileName"
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = {
            dbFilePath.toPath()
        }
    )
}
