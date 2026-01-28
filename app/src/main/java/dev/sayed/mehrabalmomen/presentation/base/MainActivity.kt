package dev.sayed.mehrabalmomen.presentation.base

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
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
fun localizedString(@StringRes id: Int, vararg args: Any): String {
    val context = rememberLocalizedContext()
    return context.getString(id, *args)
}

@Composable
fun AppRoot(settingsRepository: SettingsRepository, onReady: () -> Unit) {
    val appSettingsDelegate by settingsRepository.observeAppSettings()
        .collectAsState(initial = null)

    val currentSettings = appSettingsDelegate

    LaunchedEffect(currentSettings) {
        if (currentSettings != null) {
            onReady()
        }
    }

    if (currentSettings == null) return

    val layoutDirection = when (currentSettings.language) {
        AppSettings.Language.ARABIC -> LayoutDirection.Rtl
        else -> LayoutDirection.Ltr
    }

    CompositionLocalProvider(
        LocalAppLocale provides currentSettings.language,
        LocalLayoutDirection provides layoutDirection
    ) {
        MehrabTheme(
            isDarkTheme = when (currentSettings.theme) {
                AppSettings.Theme.SYSTEM -> isSystemInDarkTheme()
                AppSettings.Theme.DARK -> true
                AppSettings.Theme.LIGHT -> false
            }
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

fun String.toLocalizedDigits(language: AppSettings.Language): String {
    return if (language == AppSettings.Language.ARABIC) {
        this.map {
            when (it) {
                in '0'..'9' -> '٠' + (it - '0')
                else -> it
            }
        }.joinToString("")
    } else {
        this
    }
}

fun String.localizeAmPm(language: AppSettings.Language): String {
    return if (language == AppSettings.Language.ARABIC) {
        this.replace("AM", "ص").replace("PM", "م")
    } else {
        this
    }
}