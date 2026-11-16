package dev.sayed.mehrabalmomen.presentation.di

import dev.sayed.mehrabalmomen.data.azkar.local.AzkarLocalDataSource
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.calculation_method.CalculationMethodViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.PermissionsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.prayers.PrayerTimesViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.SurahAyatViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahList.SurahListViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.RecitersViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters_search.RecitersSearchViewModel
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsViewModel
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
    viewModelOf(::SettingsViewModel)
    viewModelOf(::RecitersViewModel)
    viewModelOf(::RecitersSearchViewModel)
    viewModelOf(::PrayerTimesViewModel)

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
