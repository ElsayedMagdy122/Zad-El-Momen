package dev.sayed.mehrabalmomen.presentation.screen.home

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.home.component.FeaturesSection
import dev.sayed.mehrabalmomen.presentation.screen.home.component.HomeAppBar
import dev.sayed.mehrabalmomen.presentation.screen.home.component.PrayersRowSection
import dev.sayed.mehrabalmomen.presentation.screen.home.component.UpComingPrayer
import dev.sayed.mehrabalmomen.presentation.utils.CollectEffect
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = koinViewModel()
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

    LaunchedEffect(Unit) {
        delay(2000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }


//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val alarmManager = context.getSystemService(AlarmManager::class.java)
//            if (!alarmManager.canScheduleExactAlarms()) {
//                delay(500)
//                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
//                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                }
//                try {
//                    context.startActivity(intent)
//                } catch (e: Exception) {
//                }
//            }
//        }
    }

    CollectEffect(viewModel.effect) { effect ->
        when (effect) {
            HomeEffect.NavigateToFullPrayersDetails -> {
                navController.navigate(Route.FullPrayerTimeView)
            }

            HomeEffect.NavigateToCalibrateDevice -> {
                navController.navigate(Route.CalibrateDevice)
            }

            HomeEffect.NavigateToSettings -> {
                navController.navigate(Route.SettingsScreen)
            }

            HomeEffect.NavigateToAzkar -> {
                navController.navigate(Route.AzkarScreen)
            }

            HomeEffect.NavigateToQuran -> {
                navController.navigate(Route.SurahListScreen)
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
            HomeAppBar(
                modifier = Modifier.padding(horizontal = 16.dp),
                locationUiState = state.location,
                onClickSettings = viewModel::onClickSettings
            )
        }
        item {
            UpComingPrayer(
                state = state,
                countdownTime = countdownTime,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
        item { PrayersRowSection(state.prayers, homeInteractionListener = viewModel) }
        item { FeaturesSection(homeInteractionListener = viewModel) }
    }
}