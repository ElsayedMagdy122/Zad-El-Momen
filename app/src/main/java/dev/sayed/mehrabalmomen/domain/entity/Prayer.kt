package dev.sayed.mehrabalmomen.domain.entity

data class Prayer(
    val name: PrayerName,
    val time: String,
){
    enum class PrayerName{
        FAJR,
        ZUHR,
        ASR,
        MAGHRIB,
        ISHA
    }
}

