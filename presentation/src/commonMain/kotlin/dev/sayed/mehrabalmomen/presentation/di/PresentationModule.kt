package dev.sayed.mehrabalmomen.presentation.di

import dev.sayed.mehrabalmomen.data.azkar.local.AzkarLocalDataSource
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.SurahAyatViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahList.SurahListViewModel
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.calculation_method.CalculationMethodViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.PermissionsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import zadelmomen.presentation.generated.resources.Res

val presentationModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::CalculationMethodViewModel)
    viewModelOf(::PermissionsViewModel)
    viewModelOf(::SurahListViewModel)
    viewModelOf(::SurahAyatViewModel)

    single<suspend (String) -> String>(named("asset_reader")) {
        { fileName ->
            Res.readBytes("files/$fileName").decodeToString()
        }
    }

    single<AzkarLocalDataSource> {
        AzkarLocalDataSource(
            azkarDao = get(),
            jsonProvider = {
                Res.readBytes("files/azkar.json").decodeToString()
            }
        )
    }
}
