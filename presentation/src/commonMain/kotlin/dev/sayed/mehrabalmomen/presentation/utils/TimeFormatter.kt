package dev.sayed.mehrabalmomen.presentation.utils

import dev.sayed.mehrabalmomen.domain.utils.getCurrentTimeMillis

fun convertMillisToHMS(diff: Long): Triple<String, String, String> {
    val hours = diff / 1000 / 3600
    val minutes = (diff / 1000 % 3600) / 60
    val seconds = diff / 1000 % 60

    return Triple(
        hours.toString().padStart(2, '0'),
        minutes.toString().padStart(2, '0'),
        seconds.toString().padStart(2, '0')
    )
}

fun getTimeDifference(targetMillis: Long): Long {
    val now = getCurrentTimeMillis()
    return targetMillis - now
}
