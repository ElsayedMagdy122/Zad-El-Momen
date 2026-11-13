package dev.sayed.mehrabalmomen.presentation.utils

import kotlin.time.Clock

fun convertMillisToHMS(diff: Long): Triple<String, String, String> {
    val totalSeconds = (diff / 1000).coerceAtLeast(0)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return Triple(
        hours.toString().padStart(2, '0'),
        minutes.toString().padStart(2, '0'),
        seconds.toString().padStart(2, '0')
    )
}

fun getTimeDifference(targetMillis: Long): Long {
    val now = Clock.System.now().toEpochMilliseconds()
    return targetMillis - now
}
