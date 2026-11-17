package dev.sayed.mehrabalmomen.presentation

import androidx.compose.ui.window.ComposeUIViewController
import dev.sayed.mehrabalmomen.data.di.dataModule
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.domain.di.domainModule
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.di.presentationModule
import dev.sayed.mehrabalmomen.presentation.di.presentationPlatformModule
import dev.sayed.mehrabalmomen.presentation.navigation.AppNavigation
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import platform.UIKit.UIViewController

fun createMainViewController(): UIViewController = ComposeUIViewController {
    try {
        startKoin {
            modules(presentationModule, presentationPlatformModule, domainModule, *dataModule.toTypedArray())
        }
    } catch (_: Exception) {
        // Koin already started
    }
    MehrabTheme {
        val settingsRepository: SettingsRepository = object : KoinComponent {
            val repo: SettingsRepository by inject()
        }.repo
        AppNavigation(settingsRepository = settingsRepository)
    }
}
