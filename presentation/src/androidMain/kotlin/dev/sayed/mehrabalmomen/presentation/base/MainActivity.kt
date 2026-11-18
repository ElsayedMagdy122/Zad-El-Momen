package dev.sayed.mehrabalmomen.presentation.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.navigation.AppNavigation
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        var isSettingsLoaded = false

        splashScreen.setKeepOnScreenCondition {
            !isSettingsLoaded
        }

        enableEdgeToEdge()

        setContent {
            AppRoot(settingsRepository = settingsRepository) {
                isSettingsLoaded = true
            }
        }
    }
}

@Composable
fun AppRoot(settingsRepository: SettingsRepository, onReady: () -> Unit) {
    val appSettingsDelegate by settingsRepository.observeAppSettings()
        .collectAsState(initial = null)

    val currentSettings = appSettingsDelegate
    val language = currentSettings?.language ?: AppSettings.Language.ARABIC
    setAppLocale(language)

    LaunchedEffect(currentSettings) {
        if (currentSettings != null) {
            onReady()
        }
    }

    if (currentSettings == null) return

    val isDark = when (currentSettings.theme) {
        AppSettings.Theme.SYSTEM -> isSystemInDarkTheme()
        AppSettings.Theme.DARK -> true
        AppSettings.Theme.LIGHT -> false
    }

    MehrabTheme(
        language = language,
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
