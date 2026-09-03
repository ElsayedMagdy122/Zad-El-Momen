package dev.sayed.mehrabalmomen.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.navigation.AppNavigation
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.LocalIsDarkTheme

@Composable
fun AppRoot(settingsRepository: SettingsRepository, onReady: () -> Unit = {}) {
    val appSettings by settingsRepository.observeAppSettings()
        .collectAsState(initial = null)

    LaunchedEffect(appSettings) {
        if (appSettings != null) {
            onReady()
        }
    }

    if (appSettings == null) return

    val layoutDirection = when (appSettings!!.language) {
        AppSettings.Language.ARABIC -> LayoutDirection.Rtl
        else -> LayoutDirection.Ltr
    }
    
    val isDark = when (appSettings!!.theme) {
        AppSettings.Theme.SYSTEM -> isSystemInDarkTheme()
        AppSettings.Theme.DARK -> true
        AppSettings.Theme.LIGHT -> false
    }

    CompositionLocalProvider(
        LocalAppLocale provides appSettings!!.language,
        LocalLayoutDirection provides layoutDirection,
        LocalIsDarkTheme provides isDark
    ) {
        MehrabTheme(
            language = appSettings!!.language,
            isDarkTheme = isDark
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Theme.color.surfaces.surface
            ) {
                AppNavigation(settingsRepository)
            }
        }
    }
}
