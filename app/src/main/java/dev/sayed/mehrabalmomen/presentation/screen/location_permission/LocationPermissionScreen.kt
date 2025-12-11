package dev.sayed.mehrabalmomen.presentation.screen.location_permission

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.presentation.components.PrimaryButton
import dev.sayed.mehrabalmomen.presentation.navigation.Route
import dev.sayed.mehrabalmomen.presentation.screen.calibrate_device.Steps
import dev.sayed.mehrabalmomen.presentation.screen.calibrate_device.component.stepsCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun LocationPermissionScreen(navController: NavController,viewModel: LocationViewModel = koinViewModel()) {
    val list = remember {
        listOf(
            Steps(
                icon = R.drawable.ic_accurate_prayer,
                title = "Accurate Prayer Times",
                description = "Precise calculation based on your location."
            ),
            Steps(
                icon = R.drawable.ic_qiblah,
                title = "Qibla Direction",
                description = "Guidance to the Kaaba for your prayers."
            ),
            Steps(
                icon = R.drawable.ic_protected_privacy,
                title = "Protected Privacy",
                description = "Your data is safe and secured."
            )
        )
    }
    val state by viewModel.screenState.collectAsState()
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
        viewModel.onLocationPermissionGranted()
        } else {
            viewModel.onLocationDenied()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LocationEffect.RequestLocationPermission -> {
                    launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }

                LocationEffect.NavigateToHome -> {
                    navController.navigate(Route.HomeScreen)
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Theme.color.surfaces.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
            .padding(horizontal = 16.dp)
    ) {
        Column(
            Modifier
                .align(Alignment.TopCenter)
        ) {
            LazyVerticalGrid(
                modifier = Modifier,
                contentPadding = PaddingValues(
                    bottom = 90.dp,
                    top = 64.dp
                ),
                columns = GridCells.Adaptive(320.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {

                item(span = { GridItemSpan(maxLineSpan) }) {
                    LocationCard()
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LocationHeaders()
                }
                stepsCard(list)
            }
        }
        PrimaryButton(
            isLoading = state.isLoading,
            isEnabled = state.isButtonEnabled,
            text = "Allow Location Access",
            onClick = { viewModel.onClickAllowLocationAccess()  },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        )
    }
}

@Composable
fun LocationCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(height = 80.dp, width = 80.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Theme.color.surfaces.surfaceLow),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(id = R.drawable.location_ic),
            contentDescription = null,
            tint = Theme.color.primary.primary
        )
    }
}

@Composable
fun LocationHeaders(modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Location Permission",
            color = Theme.color.primary.primary,
            style = Theme.textStyle.title.medium
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = "We need your location to calculate accurate prayer times and determine the Qibla direction.",
            color = Theme.color.primary.primary,
            style = Theme.textStyle.label.medium,
            textAlign = TextAlign.Center
        )
    }
}