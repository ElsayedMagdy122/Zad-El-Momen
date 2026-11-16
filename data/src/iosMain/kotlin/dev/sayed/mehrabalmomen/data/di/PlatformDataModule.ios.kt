package dev.sayed.mehrabalmomen.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dev.sayed.mehrabalmomen.data.bugReport.remote.BugReportRemoteDataSource
import dev.sayed.mehrabalmomen.data.bugReport.remote.IosBugReportRemoteDataSourceImpl
import dev.sayed.mehrabalmomen.data.datastore.createIosDataStore
import dev.sayed.mehrabalmomen.data.platform.audio.IosAudioPlayer
import dev.sayed.mehrabalmomen.data.platform.system.IosPermissionProvider
import dev.sayed.mehrabalmomen.data.settings.repositiory.IosBatteryOptimizationRepositoryImpl
import dev.sayed.mehrabalmomen.data.util.IosBillingManager
import dev.sayed.mehrabalmomen.data.util.network.IosNetworkConnectionRepositoryImpl
import dev.sayed.mehrabalmomen.domain.billing.BillingService
import dev.sayed.mehrabalmomen.domain.repository.audio.AudioPlayer
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.platform.PermissionProvider
import dev.sayed.mehrabalmomen.domain.repository.settings.BatteryOptimizationRepository
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    single<DataStore<Preferences>> { createIosDataStore("mehrab_settings.preferences_pb") }
    single<DataStore<Preferences>>(named("azkar_prefs")) { createIosDataStore("azkar_prefs.preferences_pb") }
    single<DataStore<Preferences>>(named("recitation_prefs")) { createIosDataStore("recitation_prefs.preferences_pb") }
    single<BillingService> { IosBillingManager() }
    single<NetworkConnectionRepository> { IosNetworkConnectionRepositoryImpl() }
    single<BugReportRemoteDataSource> { IosBugReportRemoteDataSourceImpl() }
    single<BatteryOptimizationRepository> { IosBatteryOptimizationRepositoryImpl() }
    single<PermissionProvider> { IosPermissionProvider() }
    factory<AudioPlayer> { IosAudioPlayer() }
}
