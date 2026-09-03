package dev.sayed.mehrabalmomen.presentation.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import dev.sayed.mehrabalmomen.domain.repository.companion.CompanionRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.AppRoot
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val settingsRepository: SettingsRepository by inject()
    private val companionRepository: CompanionRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        var isSettingsLoaded = false

        splashScreen.setKeepOnScreenCondition {
            !isSettingsLoaded
        }

        enableEdgeToEdge()
        
        lifecycleScope.launch {
            companionRepository.updateLastInteraction(System.currentTimeMillis())
        }

        setContent {
            AppRoot(settingsRepository = settingsRepository) {
                isSettingsLoaded = true
            }
        }
    }
}
