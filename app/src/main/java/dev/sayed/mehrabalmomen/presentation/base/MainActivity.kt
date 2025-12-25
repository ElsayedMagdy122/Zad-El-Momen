package dev.sayed.mehrabalmomen.presentation.base

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.navigation.AppNavigation
import org.koin.android.ext.android.inject
import java.util.Locale
import kotlin.time.ExperimentalTime

class MainActivity : ComponentActivity() {
    private val settingsRepository: SettingsRepository by inject()

    @OptIn(ExperimentalTime::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            AppRoot(settingsRepository = settingsRepository)
        }
    }
}


val LocalAppLocale = compositionLocalOf { AppSettings.Language.ARABIC }
@Composable
fun rememberLocalizedContext(): Context {
    val baseContext = LocalContext.current
    val language = LocalAppLocale.current

    return remember(language) {
        val config = Configuration(baseContext.resources.configuration)
        config.setLocale(Locale(language.code))
        baseContext.createConfigurationContext(config)
    }
}
@Composable
fun localizedString(@StringRes id: Int): String {
    val context = rememberLocalizedContext()
    return context.getString(id)
}
@Composable
fun AppRoot(settingsRepository: SettingsRepository) {
    val appSettings by settingsRepository.observeAppSettings()
        .collectAsState(initial = AppSettings.default)

    CompositionLocalProvider(LocalAppLocale provides appSettings.language) {
        MehrabTheme(
            isDarkTheme = when (appSettings.theme) {
                AppSettings.Theme.SYSTEM -> isSystemInDarkTheme()
                AppSettings.Theme.DARK -> true
                AppSettings.Theme.LIGHT -> false
            }
        ) {
            AppNavigation(settingsRepository)
        }
    }
}