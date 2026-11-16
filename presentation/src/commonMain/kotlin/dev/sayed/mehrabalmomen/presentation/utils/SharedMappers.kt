package dev.sayed.mehrabalmomen.presentation.utils

import dev.sayed.mehrabalmomen.design_system.base.UiText
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import org.jetbrains.compose.resources.DrawableResource
import zadelmomen.presentation.generated.resources.Res
import zadelmomen.presentation.generated.resources.asr
import zadelmomen.presentation.generated.resources.dhuhr
import zadelmomen.presentation.generated.resources.duhr
import zadelmomen.presentation.generated.resources.fajr
import zadelmomen.presentation.generated.resources.isha
import zadelmomen.presentation.generated.resources.maghrib
import zadelmomen.presentation.generated.resources.shalat_isya
import zadelmomen.presentation.generated.resources.shalat_maghrib

fun Prayer.toUiIcon(prayerName: Prayer.PrayerName): DrawableResource {
    return when (prayerName) {
        Prayer.PrayerName.FAJR -> Res.drawable.fajr
        Prayer.PrayerName.ZUHR -> Res.drawable.duhr
        Prayer.PrayerName.ASR -> Res.drawable.asr
        Prayer.PrayerName.MAGHRIB -> Res.drawable.shalat_maghrib
        Prayer.PrayerName.ISHA -> Res.drawable.shalat_isya
    }
}

fun Prayer.toUiName(prayerName: Prayer.PrayerName): UiText {
    val res = when (prayerName) {
        Prayer.PrayerName.FAJR -> Res.string.fajr
        Prayer.PrayerName.ZUHR -> Res.string.dhuhr
        Prayer.PrayerName.ASR -> Res.string.asr
        Prayer.PrayerName.MAGHRIB -> Res.string.maghrib
        Prayer.PrayerName.ISHA -> Res.string.isha
    }
    return UiText.ResString(res)
}
