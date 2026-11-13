package dev.sayed.mehrabalmomen.presentation.screen.azkar

import dev.sayed.mehrabalmomen.designsystem.resources.Res
import dev.sayed.mehrabalmomen.designsystem.resources.*
import org.jetbrains.compose.resources.DrawableResource

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

fun getBackgroundImageForCategory(title: String): DrawableResource {
    return when {
        title.contains("الصباح") -> Res.drawable.morning_adhkar
        title.contains("المساء") -> Res.drawable.evening_adhkar
        title.contains("الصلاة") -> Res.drawable.after_prayer_adhkar
        title.contains("تسابيح") -> Res.drawable.tasbeeh_adhkar
        title.contains("النوم") -> Res.drawable.bed_time_adhkar
        title.contains("الاستيقاظ") -> Res.drawable.waking_up_adhkar
        title.contains("أدعية") -> Res.drawable.quranic_duas_adhkar
        else -> Res.drawable.golden_mosque
    }
}
