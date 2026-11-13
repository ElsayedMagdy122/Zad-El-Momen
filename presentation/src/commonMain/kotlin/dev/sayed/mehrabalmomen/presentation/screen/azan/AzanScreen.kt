package dev.sayed.mehrabalmomen.presentation.screen.azan

import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.utils.toLocalizedDigits
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource

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
            localizedString(Res.string.azan_jumuah)
        else -> localizedString(getPrayerNameRes(state.prayerName))
    }

    val nextPrayerDisplayName = localizedString(getPrayerNameRes(state.nextPrayerName))
    val isAmStr = if (state.isAm) localizedString(Res.string.am) else localizedString(Res.string.pm)
    val nextIsAmStr = if (state.nextPrayerIsAm) localizedString(Res.string.am) else localizedString(Res.string.pm)

    val unknownText = localizedString(Res.string.location_unknown)
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
                title = localizedString(Res.string.azan),
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
                    text = localizedString(Res.string.time_for_prayer),
                    color = secondaryText,
                    style = Theme.textStyle.body.medium,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = localizedString(Res.string.prayer_of_format, prayerDisplayName),
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
                            style = Theme.textStyle.title.medium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = isAmStr,
                            color = secondaryText,
                            style = Theme.textStyle.label.medium
                        )
                    }
                }

                if (locationText.isNotEmpty()) {
                    Text(
                        text = locationText,
                        color = secondaryText,
                        style = Theme.textStyle.label.small,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Mosque Visual & Wave Animation Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(surfaceLow),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_mosque_light_bg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.5f
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = state.formattedElapsed.toLocalizedDigits(language),
                        color = primaryColor,
                        style = Theme.textStyle.title.extraLarge.copy(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    AzanSoundWaves(
                        isPlaying = !state.isMuted,
                        brandColor = primaryColor
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(surfaceColor.copy(alpha = 0.9f))
                            .clickable { onToggleMute() }
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (state.isMuted) Res.drawable.ic_volume_off else Res.drawable.ic_volume_on
                                ),
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = localizedString(Res.string.azan_is_playing),
                                color = primaryColor,
                                style = Theme.textStyle.label.small.copy(fontWeight = FontWeight.Medium)
                            )
                        }
                    }
                }
            }

            // Bottom Information Card & Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Next Prayer Info Banner
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(surfaceLow)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(primaryColor)
                        )
                        Text(
                            text = localizedString(Res.string.next_prayer_dot_format, nextPrayerDisplayName),
                            color = primaryColor,
                            style = Theme.textStyle.body.small.copy(fontWeight = FontWeight.Medium)
                        )
                    }

                    Text(
                        text = fullNextPrayerTime,
                        color = secondaryText,
                        style = Theme.textStyle.label.medium
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Primary Button: Stop Azan / Dismiss
                PrimaryButton(
                    text = localizedString(Res.string.azan_stop),
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )

                // Secondary Outlined Action: Mute / Unmute
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onToggleMute() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = localizedString(if (state.isMuted) Res.string.unmute_sound else Res.string.mute_sound),
                        color = primaryColor,
                        style = Theme.textStyle.label.medium.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun AzanSoundWaves(
    isPlaying: Boolean,
    brandColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val barCount = 18
        for (i in 0 until barCount) {
            val duration = 400 + (i % 5) * 120
            val delay = (i * 70) % 300

            val animatedHeightVal by infiniteTransition.animateFloat(
                initialValue = 6f,
                targetValue = 28f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = duration, delayMillis = delay, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
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

private fun getPrayerNameRes(prayerName: Prayer.PrayerName): StringResource = when (prayerName) {
    Prayer.PrayerName.FAJR -> Res.string.fajr
    Prayer.PrayerName.ZUHR -> Res.string.dhuhr
    Prayer.PrayerName.ASR -> Res.string.asr
    Prayer.PrayerName.MAGHRIB -> Res.string.maghrib
    Prayer.PrayerName.ISHA -> Res.string.isha
}

@Preview
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

@Preview
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
