package dev.sayed.mehrabalmomen.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.sayed.mehrabalmomen.data.util.createDataStoreWithDefaults
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual fun createDataStore(fileName: String): DataStore<Preferences> {
    return createDataStoreWithDefaults(
        producePath = {
            val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )
            requireNotNull(documentDirectory).path + "/$fileName"
        }
    )
}
