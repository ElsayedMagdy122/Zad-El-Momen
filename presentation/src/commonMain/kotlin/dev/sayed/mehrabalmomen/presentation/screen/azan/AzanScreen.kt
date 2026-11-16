package dev.sayed.mehrabalmomen.presentation.screen.azan

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits

@Composable
fun AzanScreen(
    state: AzanUiState,
    onDismiss: () -> Unit,
    onOpenApp: () -> Unit,
    modifier: Modifier = Modifier,
    onToggleMute: () -> Unit = {}
) {
    val language = LocalAppLocale.current

    val primaryColor = Theme.color.primary.primary
    val onPrimaryColor = Theme.color.primary.onPrimary
    val secondaryText = Theme.color.secondary.secondaryText
    val surfaceColor = Theme.color.surfaces.surface
    val surfaceLow = Theme.color.surfaces.surfaceLow

    // Prayer display names
    val prayerDisplayName = when {
        state.prayerName == Prayer.PrayerName.ZUHR && state.isFriday ->
            localizedString(R.string.azan_jumuah)
        else -> localizedString(getPrayerNameRes(state.prayerName))
    }

    val nextPrayerDisplayName = localizedString(getPrayerNameRes(state.nextPrayerName))
    val isAmStr = if (state.isAm) localizedString(R.string.am) else localizedString(R.string.pm)
    val nextIsAmStr = if (state.nextPrayerIsAm) localizedString(R.string.am) else localizedString(R.string.pm)

    val unknownText = localizedString(R.string.location_unknown)
    val locationText = when {
        state.location.isBlank() -> unknownText
        state.location.equals("Unknown", ignoreCase = true) -> unknownText
        else -> state.location
    }

    val fullNextPrayerTime = "${state.nextPrayerTimeFormatted.toLocalizedDigits(language)} $nextIsAmStr"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(surfaceColor),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Standard Design System Top App Bar
            AppBar(
                title = localizedString(R.string.azan),
                onBackClick = onOpenApp,
                isBackEnabled = true,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

            // Top Header Prayer Info
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = localizedString(R.string.time_for_prayer),
                    color = secondaryText,
                    style = Theme.textStyle.body.medium,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = localizedString(R.string.prayer_of_format, prayerDisplayName),
                    color = primaryColor,
                    style = Theme.textStyle.title.extraLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    ),
                    textAlign = TextAlign.Center
                )

                if (state.prayerTimeFormatted.isNotEmpty()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = state.prayerTimeFormatted.toLocalizedDigits(language),
                            color = primaryColor,
                            style = Theme.textStyle.title.extraLarge.copy(
                                fontSize = 44.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = isAmStr,
                            color = primaryColor,
                            style = Theme.textStyle.title.medium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Text(
                    text = locationText,
                    color = secondaryText,
                    style = Theme.textStyle.body.small,
                    textAlign = TextAlign.Center
                )
            }

            // Middle: Faded Mosque Graphic
            Image(
                painter = painterResource(R.drawable.ic_mosque_light_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1.8f)
                    .padding(vertical = 12.dp),
                contentScale = ContentScale.Fit,
                alpha = 0.85f
            )

            // Middle Bottom Cards Column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Audio Visualizer Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(surfaceLow)
                        .padding(vertical = 16.dp, horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (state.isMuted) R.drawable.ic_volume_off else R.drawable.ic_volume_on
                                ),
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = localizedString(R.string.azan_is_playing),
                                color = primaryColor,
                                style = Theme.textStyle.title.small.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        // Waveform Animation with Fixed Height Container to prevent layout shift
                        AudioWaveformVisualizer(
                            isPlaying = !state.isMuted
                        )

                        // Timer Count
                        Text(
                            text = state.formattedElapsed.toLocalizedDigits(language),
                            color = secondaryText,
                            style = Theme.textStyle.body.small
                        )
                    }
                }

                // Next Prayer Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(surfaceLow)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = fullNextPrayerTime,
                            color = primaryColor,
                            style = Theme.textStyle.title.small.copy(fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = localizedString(R.string.next_prayer_dot_format, nextPrayerDisplayName),
                            color = primaryColor,
                            style = Theme.textStyle.label.medium
                        )
                    }
                }
            }

            // Bottom Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primary Black Stop Azan Button
                PrimaryButton(
                    text = localizedString(R.string.azan_stop),
                    onClick = onDismiss,
                    enabledContainerColor = primaryColor,
                    enabledTextColor = onPrimaryColor,
                    modifier = Modifier.fillMaxWidth()
                )

                // Secondary Mute Action Text Button
                Text(
                    text = localizedString(
                        if (state.isMuted) R.string.unmute_sound else R.string.mute_sound
                    ),
                    color = primaryColor,
                    style = Theme.textStyle.label.medium,
                    modifier = Modifier
                        .clickable(onClick = onToggleMute)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AudioWaveformVisualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    barCount: Int = 16
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform")
    val brandColor = Theme.color.secondary.secondary

    Row(
        modifier = modifier.height(36.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(barCount) { index ->
            val initialVal = if (index % 2 == 0) 8f else 22f
            val targetVal = if (index % 2 == 0) 28f else 10f
            val duration = 500 + (index * 70) % 400

            val animatedHeightVal by infiniteTransition.animateFloat(
                initialValue = initialVal,
                targetValue = targetVal,
                animationSpec = infiniteRepeatable(
                    animation = tween(duration, easing = EaseInOutCubic),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$index"
            )

            val height = if (isPlaying) animatedHeightVal.dp else 6.dp

            Box(
                modifier = Modifier
                    .width(3.5.dp)
                    .height(height)
                    .clip(RoundedCornerShape(2.dp))
                    .background(brandColor)
            )
        }
    }
}

private fun getPrayerNameRes(prayerName: Prayer.PrayerName): Int = when (prayerName) {
    Prayer.PrayerName.FAJR -> R.string.fajr
    Prayer.PrayerName.ZUHR -> R.string.dhuhr
    Prayer.PrayerName.ASR -> R.string.asr
    Prayer.PrayerName.MAGHRIB -> R.string.maghrib
    Prayer.PrayerName.ISHA -> R.string.isha
}

@Preview(name = "Azan Screen Light", showBackground = true)
@Composable
private fun AzanScreenLightPreview() {
    MehrabTheme(isDarkTheme = false) {
        AzanScreen(
            state = AzanUiState(
                prayerName = Prayer.PrayerName.ZUHR,
                prayerTimeFormatted = "12:20",
                isAm = false,
                location = "بغداد، العراق",
                formattedElapsed = "00:24",
                nextPrayerName = Prayer.PrayerName.ASR,
                nextPrayerTimeFormatted = "03:48",
                nextPrayerIsAm = false
            ),
            onDismiss = {},
            onOpenApp = {}
        )
    }
}

@Preview(name = "Azan Screen Dark", showBackground = true)
@Composable
private fun AzanScreenDarkPreview() {
    MehrabTheme(isDarkTheme = true) {
        AzanScreen(
            state = AzanUiState(
                prayerName = Prayer.PrayerName.ZUHR,
                prayerTimeFormatted = "12:20",
                isAm = false,
                location = "بغداد، العراق",
                formattedElapsed = "00:24",
                nextPrayerName = Prayer.PrayerName.ASR,
                nextPrayerTimeFormatted = "03:48",
                nextPrayerIsAm = false
            ),
            onDismiss = {},
            onOpenApp = {}
        )
    }
}
