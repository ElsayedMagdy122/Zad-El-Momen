package dev.sayed.mehrabalmomen.presentation.utils

import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.asr
import dev.sayed.mehrabalmomen.designsystem.resources.dhuhr
import dev.sayed.mehrabalmomen.designsystem.resources.duhr
import dev.sayed.mehrabalmomen.designsystem.resources.fajr
import dev.sayed.mehrabalmomen.designsystem.resources.isha
import dev.sayed.mehrabalmomen.designsystem.resources.maghrib
import dev.sayed.mehrabalmomen.designsystem.resources.shalat_isya
import dev.sayed.mehrabalmomen.designsystem.resources.shalat_maghrib
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.presentation.base.UiIcon
import dev.sayed.mehrabalmomen.presentation.base.UiText

fun Prayer.toUiIcon(prayerName: Prayer.PrayerName): UiIcon {
    val res = when (prayerName) {
        Prayer.PrayerName.FAJR -> Res.drawable.fajr
        Prayer.PrayerName.ZUHR -> Res.drawable.duhr
        Prayer.PrayerName.ASR -> Res.drawable.asr
        Prayer.PrayerName.MAGHRIB -> Res.drawable.shalat_maghrib
        Prayer.PrayerName.ISHA -> Res.drawable.shalat_isya
    }
    return UiIcon(res)
}

fun Prayer.toUiName(prayerName: Prayer.PrayerName): UiText {
    val res = when (prayerName) {
        Prayer.PrayerName.FAJR -> Res.string.fajr
        Prayer.PrayerName.ZUHR -> Res.string.dhuhr
        Prayer.PrayerName.ASR -> Res.string.asr
        Prayer.PrayerName.MAGHRIB -> Res.string.maghrib
        Prayer.PrayerName.ISHA -> Res.string.isha
    }
    return UiText.Resource(res)
}
