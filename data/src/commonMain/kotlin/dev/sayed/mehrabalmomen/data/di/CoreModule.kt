package dev.sayed.mehrabalmomen.data.di

import org.koin.dsl.module
import kotlinx.serialization.json.Json

val coreModule = module {
    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            prettyPrint = false
        }
    }
}
