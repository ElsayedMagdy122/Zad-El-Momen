package dev.sayed.mehrabalmomen.presentation.screen.prayers

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.PrayerItems
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.PrayerNotifications
import dev.sayed.mehrabalmomen.presentation.screen.prayers.component.UpComingPrayerFullView
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import org.koin.androidx.compose.koinViewModel
import kotlin.time.ExperimentalTime

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@SuppressLint("BatteryLife")
@ExperimentalTime
@Composable
fun FullPrayerTimesViewScreen(
    navController: NavController,
    viewModel: FullPrayerTimesViewModel = koinViewModel()
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()
    val countdownTime by viewModel.countdownTime.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val notificationLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {

            FullPrayerTimesEffect.RequestExactAlarm -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    context.startActivity(
                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    )
                }
            }

            FullPrayerTimesEffect.RequestNotificationPermission -> {
                notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

            FullPrayerTimesEffect.RequestIgnoreBatteryOptimization -> {
                context.startActivity(
                    Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = "package:${context.packageName}".toUri()
                    }
                )
            }

            FullPrayerTimesEffect.RequestXiaomiAutoStart -> {
                openXiaomiAutoStart(context)
            }

            FullPrayerTimesEffect.NavigateBack -> {
                navController.popBackStack()
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
                countdownTime = countdownTime,
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

@SuppressLint("BatteryLife")
suspend fun checkAndRequestPermissions(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!alarmManager.canScheduleExactAlarms()) {
            context.startActivity(
                Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            )
            return
        }
    }

    val powerManager =
        context.getSystemService(Context.POWER_SERVICE) as PowerManager

    if (!powerManager.isIgnoringBatteryOptimizations(context.packageName)) {
        context.startActivity(
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = "package:${context.packageName}".toUri()
            }
        )
        return
    }

    if (Build.MANUFACTURER.equals("Xiaomi", ignoreCase = true)) {
        try {
            val intent = Intent().apply {
                component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            }
            context.startActivity(intent)
        } catch (_: Exception) {
        }
    }
}