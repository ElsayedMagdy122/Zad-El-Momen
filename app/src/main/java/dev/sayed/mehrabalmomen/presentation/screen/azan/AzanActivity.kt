package dev.sayed.mehrabalmomen.presentation.screen.azan

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.model.AppSettings
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.MainActivity
import dev.sayed.mehrabalmomen.presentation.service.PrayerAlarmService
import dev.sayed.mehrabalmomen.presentation.utils.Constants.ACTION_STOP_AZAN_UI
import dev.sayed.mehrabalmomen.presentation.utils.Constants.PRAYER_NAME_KEY
import dev.sayed.mehrabalmomen.presentation.utils.Constants.PRAYER_TIME_MILLIS_KEY
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class AzanActivity : ComponentActivity() {

    private val azanViewModel: AzanViewModel by viewModel()
    private val settingsRepository: SettingsRepository by inject()

    private val stopAzanUiReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == ACTION_STOP_AZAN_UI) {
                dismissActivity()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        showWhenLocked()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        registerStopAzanReceiver()
        handleIntent(intent)

        onBackPressedDispatcher.addCallback(this) {
            stopAzanAndFinish()
        }

        setContent {
            val appSettings by settingsRepository.observeAppSettings()
                .collectAsState(initial = null)

            val currentSettings = appSettings ?: return@setContent

            val isDark = when (currentSettings.theme) {
                AppSettings.Theme.SYSTEM -> isSystemInDarkTheme()
                AppSettings.Theme.DARK -> true
                AppSettings.Theme.LIGHT -> false
            }
            val layoutDirection = when (currentSettings.language) {
                AppSettings.Language.ARABIC -> LayoutDirection.Rtl
                else -> LayoutDirection.Ltr
            }

            CompositionLocalProvider(
                LocalAppLocale provides currentSettings.language,
                LocalLayoutDirection provides layoutDirection
            ) {
                MehrabTheme(
                    language = currentSettings.language,
                    isDarkTheme = isDark
                ) {
                    val state by azanViewModel.state.collectAsState()
                    AzanScreen(
                        state = state,
                        onDismiss = { stopAzanAndFinish() },
                        onOpenApp = { openMainActivity() },
                        onToggleMute = { azanViewModel.toggleMute() }
                    )
                }
            }
        }
    }

    private fun registerStopAzanReceiver() {
        val filter = IntentFilter(ACTION_STOP_AZAN_UI)
        ContextCompat.registerReceiver(this, stopAzanUiReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED)
    }

    override fun onDestroy() {
        try {
            unregisterReceiver(stopAzanUiReceiver)
        } catch (_: Exception) {
        }
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val prayerName = intent?.getStringExtra(PRAYER_NAME_KEY) ?: "FAJR"
        val prayerTimeMillis = intent?.getLongExtra(PRAYER_TIME_MILLIS_KEY, 0L) ?: 0L
        val prayerEnum = runCatching { Prayer.PrayerName.valueOf(prayerName) }
            .getOrDefault(Prayer.PrayerName.FAJR)
        azanViewModel.setPrayerInfo(prayerEnum, prayerTimeMillis)

        // Only start the audio service if it is not already running
        if (!PrayerAlarmService.isRunning()) {
            try {
                val serviceIntent = Intent(this, PrayerAlarmService::class.java).apply {
                    putExtra(PRAYER_NAME_KEY, prayerName)
                    putExtra(PRAYER_TIME_MILLIS_KEY, prayerTimeMillis)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
            } catch (_: Exception) {
            }
        }
    }

    private fun stopAzanAndFinish() {
        // 1. Instantly stop audio playback and dismiss notification
        PrayerAlarmService.stopAzan()
        try {
            stopService(Intent(this, PrayerAlarmService::class.java))
        } catch (_: Exception) {
        }
        // 2. Dismiss activity context-aware
        dismissActivity()
    }

    private fun dismissActivity() {
        if (isTaskRoot) {
            finishAndRemoveTask()
        } else {
            finish()
        }
    }

    private fun openMainActivity() {
        // 1. Stop Azan audio
        PrayerAlarmService.stopAzan()
        try {
            stopService(Intent(this, PrayerAlarmService::class.java))
        } catch (_: Exception) {
        }

        // 2. Navigate to MainActivity
        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("FROM_AZAN", true)
        }
        startActivity(mainIntent)
        finishAndRemoveTask()
    }

    private fun showWhenLocked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        @Suppress("DEPRECATION")
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                    WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )

        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            keyguardManager?.requestDismissKeyguard(this, null)
        }
    }
}
