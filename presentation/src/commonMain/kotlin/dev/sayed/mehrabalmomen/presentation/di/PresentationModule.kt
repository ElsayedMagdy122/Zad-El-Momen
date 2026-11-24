package dev.sayed.mehrabalmomen.presentation.di

import dev.sayed.mehrabalmomen.data.azkar.local.AzkarLocalDataSource
import dev.sayed.mehrabalmomen.presentation.recitation.HighlightingViewModel
import dev.sayed.mehrabalmomen.presentation.screen.azkar.AzkarViewModel
import dev.sayed.mehrabalmomen.presentation.screen.AzkarDetails.AzkarDetailViewModel
import dev.sayed.mehrabalmomen.presentation.screen.azan.AzanViewModel
import dev.sayed.mehrabalmomen.presentation.screen.radio.RadioChannelsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.home.HomeViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.calculation_method.CalculationMethodViewModel
import dev.sayed.mehrabalmomen.presentation.screen.onBoarding.permissions.PermissionsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.prayers.PrayerTimesViewModel
import dev.sayed.mehrabalmomen.presentation.screen.qiblah.QiblahViewModel
import dev.sayed.mehrabalmomen.presentation.screen.SearchAyah.SearchAyahViewModel
import dev.sayed.mehrabalmomen.presentation.screen.bookmarks.BookMarkListViewModel
import dev.sayed.mehrabalmomen.presentation.screen.reminders.ReminderSettingsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahAyat.SurahAyatViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.SurahList.SurahListViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters.RecitersViewModel
import dev.sayed.mehrabalmomen.presentation.screen.quran.reciters_search.RecitersSearchViewModel
import dev.sayed.mehrabalmomen.presentation.screen.settings.SettingsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.settings.contact_us.ContactViewModel
import dev.sayed.mehrabalmomen.presentation.screen.maps.MapsViewModel
import dev.sayed.mehrabalmomen.presentation.screen.ReportBug.ReportBugViewModel
import dev.sayed.mehrabalmomen.presentation.screen.companion.CompanionViewModel
import dev.sayed.mehrabalmomen.presentation.screen.notifications.NotificationsViewModel
import org.koin.core.module.dsl.viewModel
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
    viewModelOf(::MapsViewModel)
    viewModelOf(::RecitersViewModel)
    viewModelOf(::RecitersSearchViewModel)
    viewModelOf(::PrayerTimesViewModel)
    viewModelOf(::AzkarViewModel)
    viewModelOf(::AzkarDetailViewModel)
    viewModelOf(::RadioChannelsViewModel)
    viewModelOf(::QiblahViewModel)
    viewModelOf(::SearchAyahViewModel)
    viewModelOf(::BookMarkListViewModel)
    viewModelOf(::ReminderSettingsViewModel)
    viewModelOf(::AzanViewModel)
    viewModelOf(::ReportBugViewModel)
    viewModelOf(::ContactViewModel)
    viewModelOf(::CompanionViewModel)
    viewModelOf(::NotificationsViewModel)

    viewModel {
        HighlightingViewModel(
            modelDownloader = get(),
            settingsRepository = get(),
            audioPipeline = get(),
            sherpaEngine = get(),
            assetReader = get(named("asset_reader"))
        )
    }

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
