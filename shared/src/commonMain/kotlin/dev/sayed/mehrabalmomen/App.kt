package dev.sayed.mehrabalmomen

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.LocalIsDarkTheme
import dev.sayed.mehrabalmomen.presentation.navigation.AppNavigation
import org.koin.compose.koinInject

@Composable
@Preview
fun App(
    settingsRepository: SettingsRepository = koinInject()
) {
    val currentSettings by settingsRepository.observeAppSettings().collectAsState(
        initial = null
    )

    if (currentSettings == null) return

    val settings = currentSettings!!
    val isDark = when (settings.theme) {
        AppSettings.Theme.SYSTEM -> isSystemInDarkTheme()
        AppSettings.Theme.DARK -> true
        AppSettings.Theme.LIGHT -> false
    }

    CompositionLocalProvider(
        LocalAppLocale provides settings.language,
        LocalIsDarkTheme provides isDark
    ) {
        MehrabTheme(
            language = settings.language,
            isDarkTheme = isDark
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Theme.color.surfaces.surface
            ) {
                AppNavigation(settingsRepository = settingsRepository)
            }
        }
    }
}
