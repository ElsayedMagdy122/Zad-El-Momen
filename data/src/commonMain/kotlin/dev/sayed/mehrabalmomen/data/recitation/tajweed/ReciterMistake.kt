package dev.sayed.mehrabalmomen.data.recitation.tajweed

enum class ReciterErrorSeverity {
    TAJWEED,
    TASHKEEL,
    PHONETIC
}

enum class TajweedRuleType(
    val title: String,
    val advice: String,
    val goldenLen: Int
) {
    NORMAL_MADD("المد الطبيعي", "حافظ على مقدار المد الطبيعي (حركتان)", 2),
    MUTTASEL_MADD("المد المتصل", "أطل المد المتصل بمقدار 4 إلى 5 حركات", 4),
    MUNFASEL_MADD("المد المنفصل", "أطل المد المنفصل بمقدار 4 إلى 5 حركات", 4),
    AARED_MADD("المد العارض للسكون", "المد العارض بمقدار 2 أو 4 أو 6 حركات", 4),
    LAZEM_MADD("المد اللازم", "المد اللازم بمقدار 6 حركات كاملة", 6),
    LEEN_MADD("مد اللين", "مد اللين بمقدار 2 أو 4 أو 6 حركات", 2),
    MUSHADDAD_GHUNNAH("الغنّة المشددة", "أعطِ الغنّة المشددة حقها (حركتان)", 2),
    SHADDAH("النطق بالشدة", "اضغط على الحرف المشدد ببيان واستطالة حركية", 1);

    companion object {
        fun fromRule(rule: TajweedRule?): TajweedRuleType = when (rule) {
            is LazemMaddRule -> LAZEM_MADD
            is MottaselMaddRule, is MottaselMaddPauseRule -> MUTTASEL_MADD
            is MonfaselMaddRule -> MUNFASEL_MADD
            is AaredMaddRule -> AARED_MADD
            is LeenMaddRule -> LEEN_MADD
            is MushaddadGhunnahRule -> MUSHADDAD_GHUNNAH
            is ShaddahRule -> SHADDAH
            else -> NORMAL_MADD
        }
    }
}

sealed class ReciterMistake(
    val severity: ReciterErrorSeverity,
    val advice: String
) {
    data class UnderheldDuration(
        val ruleType: TajweedRuleType,
        val actualSeconds: Double,
        val expectedSeconds: Double,
        val goldenHarakat: Int = ruleType.goldenLen
    ) : ReciterMistake(ReciterErrorSeverity.TAJWEED, ruleType.advice)

    data class OverheldDuration(
        val ruleType: TajweedRuleType,
        val actualSeconds: Double,
        val maxAllowedSeconds: Double
    ) : ReciterMistake(ReciterErrorSeverity.TAJWEED, "تنبيه: تم الإطالة في مقدار المد أكثر من الحد المسموح")

    data class TashkeelMismatch(
        val expectedPhoneme: String,
        val actualPhoneme: String
    ) : ReciterMistake(ReciterErrorSeverity.TASHKEEL, "انتبه لتشكيل الحرف وحركته الصحيحة")

    data class LetterOmitted(
        val omittedLetter: String
    ) : ReciterMistake(ReciterErrorSeverity.PHONETIC, "انتبه لنطق جميع حروف الكلمة دون إغفال")

    data class LetterSubstituted(
        val expectedLetter: String,
        val actualLetter: String
    ) : ReciterMistake(ReciterErrorSeverity.PHONETIC, "تأكد من مخارج الحروف وعدم إبدال حرف بآخر")
}
