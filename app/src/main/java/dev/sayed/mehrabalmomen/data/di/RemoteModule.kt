package dev.sayed.mehrabalmomen.data.di

import dev.sayed.mehrabalmomen.BuildConfig
import dev.sayed.mehrabalmomen.data.bugReport.remote.BugReportRemoteDataSource
import dev.sayed.mehrabalmomen.data.bugReport.remote.BugReportRemoteDataSourceImpl
import dev.sayed.mehrabalmomen.data.bugReport.remote.BugReportRpcService
import dev.sayed.mehrabalmomen.data.bugReport.remote.BugReportStorageService
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioReadersRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioReadersRemoteDataSourceImpl
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioRemoteDataSourceImpl
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioTimingsRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioTimingsRemoteDataSourceImpl
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
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
    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(get())
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 30_000
                socketTimeoutMillis = 30_000
            }
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
    single <QuranAudioReadersRemoteDataSource>{ QuranAudioReadersRemoteDataSourceImpl(get()) }
    single <QuranAudioRemoteDataSource>{ QuranAudioRemoteDataSourceImpl(get()) }
    single <QuranAudioTimingsRemoteDataSource>{ QuranAudioTimingsRemoteDataSourceImpl(get()) }
}