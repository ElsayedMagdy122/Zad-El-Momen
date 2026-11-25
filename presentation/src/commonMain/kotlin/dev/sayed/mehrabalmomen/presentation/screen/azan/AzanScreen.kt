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
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.sayed.mehrabalmomen.design_system.component.AppBar
import dev.sayed.mehrabalmomen.design_system.component.PrimaryButton
import dev.sayed.mehrabalmomen.design_system.theme.MehrabTheme
import dev.sayed.mehrabalmomen.design_system.theme.Theme
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.presentation.base.LocalAppLocale
import dev.sayed.mehrabalmomen.presentation.base.localizedString
import dev.sayed.mehrabalmomen.presentation.base.toLocalizedDigits
import dev.sayed.mehrabalmomen.presentation.utils.bounceClick
import dev.sayed.mehrabalmomen.presentation.utils.pulsate
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.azan
import zadelmomen.presentation.generated.resources.azan_is_playing
import zadelmomen.presentation.generated.resources.azan_jumuah
import zadelmomen.presentation.generated.resources.azan_stop
import zadelmomen.presentation.generated.resources.am
import zadelmomen.presentation.generated.resources.asr
import zadelmomen.presentation.generated.resources.dhuhr
import zadelmomen.presentation.generated.resources.fajr
import zadelmomen.presentation.generated.resources.ic_close_circle
import zadelmomen.presentation.generated.resources.ic_mosque_light_bg
import zadelmomen.presentation.generated.resources.ic_volume_off
import zadelmomen.presentation.generated.resources.ic_volume_on
import zadelmomen.presentation.generated.resources.isha
import zadelmomen.presentation.generated.resources.location_unknown
import zadelmomen.presentation.generated.resources.maghrib
import zadelmomen.presentation.generated.resources.mute_sound
import zadelmomen.presentation.generated.resources.next_prayer_dot_format
import zadelmomen.presentation.generated.resources.pm
import zadelmomen.presentation.generated.resources.prayer_of_format
import zadelmomen.presentation.generated.resources.time_for_prayer
import zadelmomen.presentation.generated.resources.unmute_sound

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
            AppBar(
                title = localizedString(Res.string.azan),
                onBackClick = onOpenApp,
                isBackEnabled = true,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )

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

            Image(
                painter = painterResource(Res.drawable.ic_mosque_light_bg),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1.8f)
                    .padding(vertical = 12.dp)
                    .pulsate(enabled = !state.isMuted, minAlpha = 0.75f, maxAlpha = 1.0f, translationYPx = 2f),
                contentScale = ContentScale.Fit,
                alpha = 0.85f
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                                    if (state.isMuted) Res.drawable.ic_volume_off else Res.drawable.ic_volume_on
                                ),
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = localizedString(Res.string.azan_is_playing),
                                color = primaryColor,
                                style = Theme.textStyle.title.small.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        AudioWaveformVisualizer(
                            isPlaying = !state.isMuted
                        )

                        Text(
                            text = state.formattedElapsed.toLocalizedDigits(language),
                            color = secondaryText,
                            style = Theme.textStyle.body.small
                        )
                    }
                }

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
                            text = localizedString(Res.string.next_prayer_dot_format, nextPrayerDisplayName),
                            color = primaryColor,
                            style = Theme.textStyle.label.medium
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryButton(
                    text = localizedString(Res.string.azan_stop),
                    onClick = onDismiss,
                    enabledContainerColor = primaryColor,
                    enabledTextColor = onPrimaryColor,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = localizedString(
                        if (state.isMuted) Res.string.unmute_sound else Res.string.mute_sound
                    ),
                    color = primaryColor,
                    style = Theme.textStyle.label.medium,
                    modifier = Modifier
                        .bounceClick(scaleDown = 0.92f) { onToggleMute() }
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

private fun getPrayerNameRes(prayerName: Prayer.PrayerName): org.jetbrains.compose.resources.StringResource = when (prayerName) {
    Prayer.PrayerName.FAJR -> Res.string.fajr
    Prayer.PrayerName.ZUHR -> Res.string.dhuhr
    Prayer.PrayerName.ASR -> Res.string.asr
    Prayer.PrayerName.MAGHRIB -> Res.string.maghrib
    Prayer.PrayerName.ISHA -> Res.string.isha
}
