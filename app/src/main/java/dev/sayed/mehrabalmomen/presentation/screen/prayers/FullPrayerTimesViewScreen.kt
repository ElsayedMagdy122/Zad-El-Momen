package dev.sayed.mehrabalmomen.presentation.screen.prayers

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.PrayerItems
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.PrayerNotifications
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.UpComingPrayerFullView
import org.koin.androidx.compose.koinViewModel
import kotlin.time.ExperimentalTime

@ExperimentalTime
@Composable
fun FullPrayerTimesViewScreen(
    navController: NavController,
    viewModel: FullPrayerTimesViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {

                FullPrayerTimesEffect.RequestExactAlarm -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        context.startActivity(
                            Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                        )
                    }
                }

                FullPrayerTimesEffect.RequestIgnoreBatteryOptimization -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        context.startActivity(
                            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                        )
                    }
                }

                FullPrayerTimesEffect.RequestXiaomiAutoStart -> {
                    openXiaomiAutoStart(context)
                }

                FullPrayerTimesEffect.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        item {
            AppBar(
                onBackClick = viewModel::onClickBack,
                title = localizedString(R.string.prayer_times),
                modifier = Modifier.padding(
                    horizontal = 16.dp
                )
            )
        }
        item {
            UpComingPrayerFullView(
                state = state,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        item {
            PrayerItems(
                prayers = state.prayers,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        item {
            PrayerNotifications(
                prayerNotifications = state.prayerNotifications,
                listener = viewModel,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

fun openXiaomiAutoStart(context: Context) {
    try {
        val intent = Intent().apply {
            component = ComponentName(
                "com.miui.securitycenter",
                "com.miui.permcenter.autostart.AutoStartManagementActivity"
            )
        }
        context.startActivity(intent)
    } catch (e: Exception) {
    }
}