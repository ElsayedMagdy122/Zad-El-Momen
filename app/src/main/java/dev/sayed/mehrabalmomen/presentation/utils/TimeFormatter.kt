package dev.sayed.mehrabalmomen.presentation.utils

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
 fun convertMillisToHMS(diff: Long): Triple<String, String, String> {
    val hours = diff / 1000 / 3600
    val minutes = (diff / 1000 % 3600) / 60
    val seconds = diff / 1000 % 60

    return Triple(
        String.format("%02d", hours),
        String.format("%02d", minutes),
        String.format("%02d", seconds)
    )
}

 fun getTimeDifference(targetMillis: Long): Long {
    val now = System.currentTimeMillis()
    return targetMillis - now
}
