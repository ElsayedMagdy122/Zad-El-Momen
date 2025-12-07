package dev.sayed.mehrabalmomen.presentation.screen.home

import dev.sayed.mehrabalmomen.R
import dev.sayed.mehrabalmomen.domain.entity.Prayer
import dev.sayed.mehrabalmomen.presentation.utils.format
import kotlinx.datetime.TimeZone
import kotlin.time.ExperimentalTime

fun Prayer.toUiIcon(prayerName: Prayer.PrayerName):Int{
    return when(prayerName){
        Prayer.PrayerName.FAJR -> R.drawable.fajr
        Prayer.PrayerName.ZUHR ->  R.drawable.duhr
        Prayer.PrayerName.ASR ->  R.drawable.asr
        Prayer.PrayerName.MAGHRIB ->  R.drawable.shalat_maghrib
        Prayer.PrayerName.ISHA ->  R.drawable.shalat_isya
    }
}
fun Prayer.toUiName(prayerName: Prayer.PrayerName):Int{
    return when(prayerName){
        Prayer.PrayerName.FAJR -> R.string.fajr
        Prayer.PrayerName.ZUHR ->  R.string.dhuhr
        Prayer.PrayerName.ASR ->  R.string.asr
        Prayer.PrayerName.MAGHRIB ->  R.string.maghrib
        Prayer.PrayerName.ISHA ->  R.string.isha
    }
}
@OptIn(ExperimentalTime::class)
fun Prayer.toPrayerUiState(zone: TimeZone): HomeUiState.PrayerUiState{
    return HomeUiState.PrayerUiState(
        name = this.toUiName(this.name),
        time = format(instant = this.time, zone = zone),
        icon = this.toUiIcon(this.name)
    )
}

