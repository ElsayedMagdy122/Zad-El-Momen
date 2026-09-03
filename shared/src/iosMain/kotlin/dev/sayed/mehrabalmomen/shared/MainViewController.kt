package dev.sayed.mehrabalmomen.shared

import androidx.compose.ui.window.ComposeUIViewController
import dev.sayed.mehrabalmomen.presentation.AppRoot
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    val koinComponent = object : KoinComponent {
        val settingsRepository: SettingsRepository by inject()
    }
    AppRoot(koinComponent.settingsRepository)
}
