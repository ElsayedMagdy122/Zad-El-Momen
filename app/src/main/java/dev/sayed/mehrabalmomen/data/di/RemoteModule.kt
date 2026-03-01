package dev.sayed.mehrabalmomen.data.di

import dev.sayed.mehrabalmomen.BuildConfig
import dev.sayed.mehrabalmomen.data.remote.BugReportRemoteDataSource
import dev.sayed.mehrabalmomen.data.remote.BugReportRemoteDataSourceImpl
import dev.sayed.mehrabalmomen.data.remote.BugReportRpcService
import dev.sayed.mehrabalmomen.data.remote.BugReportStorageService
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.client.plugins.HttpTimeout
import kotlinx.serialization.json.Json
import org.koin.dsl.module

@OptIn(SupabaseInternal::class)
val remoteModule = module {
    single {
        val supabase = createSupabaseClient(
            supabaseKey = BuildConfig.SUPABASE_KEY,
            supabaseUrl = BuildConfig.SUPABASE_URL,
        ) {
            install(Realtime)
            install(Postgrest)
            install(Storage)
            httpConfig {
                install(HttpTimeout) {
                    requestTimeoutMillis = 10_000
                    connectTimeoutMillis = 15_000
                    socketTimeoutMillis = 15_000
                }
            }
        }
        supabase
    }

    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
            prettyPrint = false
        }
    }
    single<BugReportRemoteDataSource> {
        BugReportRemoteDataSourceImpl(
            rpcService = get(),
            storageService = get(),
            supabase = get(),
            context = get()
        )
    }
    single {
        BugReportStorageService(
            supabase = get()
        )
    }
    single {
        BugReportRpcService(
            supabase = get()
        )
    }
}