package dev.sayed.mehrabalmomen.presentation

import androidx.compose.ui.window.ComposeUIViewController
import dev.sayed.mehrabalmomen.data.di.dataModule
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.domain.di.domainModule
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.di.presentationModule
import dev.sayed.mehrabalmomen.presentation.navigation.AppNavigation
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.UIKit.UIViewController

fun createMainViewController(): UIViewController = ComposeUIViewController {
    try {
        org.koin.core.context.GlobalContext.get()
    } catch (_: Exception) {
        org.koin.core.context.GlobalContext.startKoin {
            modules(presentationModule, domainModule, *dataModule.toTypedArray())
        }
    }
    MehrabTheme {
        val settingsRepository: SettingsRepository = object : KoinComponent {
            val repo: SettingsRepository by inject()
        }.repo
        AppNavigation(settingsRepository = settingsRepository)
    }
}
