package dev.sayed.mehrabalmomen.presentation.screen.azkar

import androidx.annotation.DrawableRes
import dev.sayed.mehrabalmomen.R

fun String.toAzkarType(): AzkarType = when (this) {
    "أذكار الصباح" -> AzkarType.MORNING
    "أذكار المساء" -> AzkarType.EVENING
    "أذكار بعد الصلاة المفروضة" -> AzkarType.AFTER_PRAYER
    "تسابيح" -> AzkarType.TASBIH
    "أذكار النوم" -> AzkarType.SLEEP
    "أذكار الاستيقاظ" -> AzkarType.WAKE
    "أدعية قرآنية" -> AzkarType.QURAN_DUA
    "أدعية الأنبياء" -> AzkarType.PROPHETS_DUA
    else -> AzkarType.MORNING
}

@DrawableRes
fun getBackgroundImageForCategory(title: String): Int {
    return when {
        title.contains("الصباح") -> R.drawable.morning_adhkar
        title.contains("المساء") -> R.drawable.evening_adhkar
        title.contains("الصلاة") -> R.drawable.after_prayer_adhkar
        title.contains("تسابيح") -> R.drawable.tasbeeh_adhkar
        title.contains("النوم") -> R.drawable.bed_time_adhkar
        title.contains("الاستيقاظ") -> R.drawable.waking_up_adhkar
        title.contains("أدعية") -> R.drawable.quranic_duas_adhkar
        else -> R.drawable.golden_mosque
    }
}
