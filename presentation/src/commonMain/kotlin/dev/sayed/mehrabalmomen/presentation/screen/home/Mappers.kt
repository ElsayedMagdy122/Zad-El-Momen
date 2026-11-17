package dev.sayed.mehrabalmomen.presentation.screen.home

import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.presentation.base.UiIcon
import dev.sayed.mehrabalmomen.presentation.base.UiText
import dev.sayed.mehrabalmomen.presentation.utils.format
import kotlinx.datetime.TimeZone
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.asr
import zadelmomen.presentation.generated.resources.dhuhr
import zadelmomen.presentation.generated.resources.duhr
import zadelmomen.presentation.generated.resources.fajr
import zadelmomen.presentation.generated.resources.isha
import zadelmomen.presentation.generated.resources.maghrib
import zadelmomen.presentation.generated.resources.shalat_isya
import zadelmomen.presentation.generated.resources.shalat_maghrib
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Prayer.toPrayerUiState(zone: TimeZone): HomeUiState.PrayerUiState {
    val formatted = format(instant = this.time, zone = zone)
    val nameRes = when (this.name) {
        Prayer.PrayerName.FAJR -> Res.string.fajr
        Prayer.PrayerName.ZUHR -> Res.string.dhuhr
        Prayer.PrayerName.ASR -> Res.string.asr
        Prayer.PrayerName.MAGHRIB -> Res.string.maghrib
        Prayer.PrayerName.ISHA -> Res.string.isha
    }
    val iconRes = when (this.name) {
        Prayer.PrayerName.FAJR -> Res.drawable.fajr
        Prayer.PrayerName.ZUHR -> Res.drawable.duhr
        Prayer.PrayerName.ASR -> Res.drawable.asr
        Prayer.PrayerName.MAGHRIB -> Res.drawable.shalat_maghrib
        Prayer.PrayerName.ISHA -> Res.drawable.shalat_isya
    }
    return HomeUiState.PrayerUiState(
        prayerName = this.name,
        name = UiText.StringRes(nameRes),
        time = formatted.time,
        isAm = formatted.isAm,
        icon = UiIcon(iconRes)
    )
}
