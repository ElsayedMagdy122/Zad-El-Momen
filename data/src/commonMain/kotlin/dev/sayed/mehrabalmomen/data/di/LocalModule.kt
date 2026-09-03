package dev.sayed.mehrabalmomen.data.di

import dev.sayed.mehrabalmomen.data.local.createDataStore
import dev.sayed.mehrabalmomen.data.settings.local.RecitationPreferences
import org.koin.dsl.module
import org.koin.core.qualifier.named

val localModule = module {
    single(named("settings_ds")) { createDataStore("location_settings.preferences_pb") }
    
    single(named("recitation_prefs_ds")) { 
        createDataStore("recitation_settings.preferences_pb") 
    }
    
    single { RecitationPreferences(get(named("recitation_prefs_ds"))) }
    
    // For repositories that just use "get()" for DataStore, we might need a default or named ones
}
