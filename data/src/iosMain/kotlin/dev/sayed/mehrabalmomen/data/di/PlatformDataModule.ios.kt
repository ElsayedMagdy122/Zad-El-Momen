package dev.sayed.mehrabalmomen.data.di

import dev.sayed.mehrabalmomen.data.bugReport.remote.BugReportRemoteDataSource
import dev.sayed.mehrabalmomen.data.bugReport.remote.IosBugReportRemoteDataSourceImpl
import dev.sayed.mehrabalmomen.data.settings.repositiory.IosBatteryOptimizationRepositoryImpl
import dev.sayed.mehrabalmomen.data.util.IosBillingManager
import dev.sayed.mehrabalmomen.data.util.network.IosNetworkConnectionRepositoryImpl
import dev.sayed.mehrabalmomen.domain.billing.BillingService
import dev.sayed.mehrabalmomen.domain.repository.network.NetworkConnectionRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.BatteryOptimizationRepository
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformDataModule: Module = module {
    single<BillingService> { IosBillingManager() }
    single<NetworkConnectionRepository> { IosNetworkConnectionRepositoryImpl() }
    single<BugReportRemoteDataSource> { IosBugReportRemoteDataSourceImpl() }
    single<BatteryOptimizationRepository> { IosBatteryOptimizationRepositoryImpl() }
    single<suspend (String) -> String>(named("asset_reader")) {
        val reader: suspend (String) -> String = { _: String -> "" }
        reader
    }
}
