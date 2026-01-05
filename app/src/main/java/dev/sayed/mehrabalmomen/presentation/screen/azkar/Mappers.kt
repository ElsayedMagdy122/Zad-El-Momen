package dev.sayed.mehrabalmomen.presentation.screen.azkar

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