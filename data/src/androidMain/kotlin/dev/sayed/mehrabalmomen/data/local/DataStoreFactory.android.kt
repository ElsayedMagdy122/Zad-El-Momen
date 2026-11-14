package dev.sayed.mehrabalmomen.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import okio.Path.Companion.toPath
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class DataStoreFactory : KoinComponent {
    private val context: Context by inject()

    actual fun create(): DataStore<Preferences> {
        return PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                context.filesDir.resolve("settings.preferences_pb").absolutePath.toPath()
            }
        )
    }
}
