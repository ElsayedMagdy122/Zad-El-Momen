package dev.sayed.mehrabalmomen.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dev.sayed.mehrabalmomen.data.util.createDataStoreWithDefaults
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun createDataStore(fileName: String): DataStore<Preferences> {
    val context = object : KoinComponent {
        val context: Context by inject()
    }.context
    
    return createDataStoreWithDefaults(
        producePath = { context.preferencesDataStoreFile(fileName).absolutePath }
    )
}
