package dev.sayed.mehrabalmomen.presentation.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.domain.repository.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.navigation.AppNavigation
import org.koin.android.ext.android.inject
import kotlin.time.ExperimentalTime

class MainActivity : ComponentActivity() {
    private val settingsRepository: SettingsRepository by inject()

    @OptIn(ExperimentalTime::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            MehrabTheme(isDarkTheme = isSystemInDarkTheme()) {
                AppNavigation(settingsRepository)
            }
        }
    }
}
