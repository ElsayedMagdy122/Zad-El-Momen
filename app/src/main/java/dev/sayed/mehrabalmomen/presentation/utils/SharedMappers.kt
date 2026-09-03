package dev.sayed.mehrabalmomen.presentation.utils

import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.design_system.utils.UiIcon
import dev.sayed.mehrabalmomen.design_system.utils.UiText
import dev.sayed.mehrabalmomen.R 

fun Prayer.toUiIcon(prayerName: Prayer.PrayerName): UiIcon {
    val resId = when(prayerName){
        Prayer.PrayerName.FAJR -> R.drawable.fajr
        Prayer.PrayerName.ZUHR ->  R.drawable.duhr
        Prayer.PrayerName.ASR ->  R.drawable.asr
        Prayer.PrayerName.MAGHRIB ->  R.drawable.shalat_maghrib
        Prayer.PrayerName.ISHA ->  R.drawable.shalat_isya
    }
    return UiIcon(resId)
}

fun Prayer.toUiName(prayerName: Prayer.PrayerName): UiText {
    val resId = when(prayerName){
        Prayer.PrayerName.FAJR -> R.string.fajr
        Prayer.PrayerName.ZUHR ->  R.string.dhuhr
        Prayer.PrayerName.ASR ->  R.string.asr
        Prayer.PrayerName.MAGHRIB ->  R.string.maghrib
        Prayer.PrayerName.ISHA ->  R.string.isha
    }
    return UiText.StringResource(resId)
}
