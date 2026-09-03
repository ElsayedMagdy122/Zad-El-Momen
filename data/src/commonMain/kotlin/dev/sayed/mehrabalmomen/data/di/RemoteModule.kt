package dev.sayed.mehrabalmomen.data.di

import dev.sayed.mehrabalmomen.data.bugReport.remote.BugReportRemoteDataSource
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
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

@OptIn(SupabaseInternal::class)
val remoteModule = module {
    single {
        val supabase = createSupabaseClient(
            supabaseKey = "YOUR_SUPABASE_KEY", // Should be injected or use a BuildConfig counterpart
            supabaseUrl = "YOUR_SUPABASE_URL",
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
    
    // HttpClient engine will be provided per platform or using a default
    // We might need to split this part or use a default engine
    /*
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(get())
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 300_000
                connectTimeoutMillis = 300_000
                socketTimeoutMillis = 300_000
            }
        }
    }
    */
    
    single { BugReportStorageService(supabase = get()) }
    single { BugReportRpcService(supabase = get()) }
    
    single<QuranAudioReadersRemoteDataSource> { QuranAudioReadersRemoteDataSourceImpl(get()) }
    single<QuranAudioRemoteDataSource> { QuranAudioRemoteDataSourceImpl(get()) }
    single<QuranAudioTimingsRemoteDataSource> { QuranAudioTimingsRemoteDataSourceImpl(get()) }
}
