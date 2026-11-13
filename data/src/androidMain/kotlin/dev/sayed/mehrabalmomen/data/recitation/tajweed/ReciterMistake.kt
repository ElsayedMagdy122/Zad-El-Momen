package dev.sayed.mehrabalmomen.data.recitation.tajweed

/**
 * التصنيف العام لنوع الخطأ لتحديد اللون والأولوية في الواجهة.
 */
enum class ReciterErrorSeverity {
    TAJWEED,   // أحكام تجويد وزمن مدود/غنن (أصفر)
    TASHKEEL,  // خطأ في تشكيل وحركات الحرف (أصفر/برتقالي)
    PHONETIC   // إسقاط حرف أو إبداله بحرف آخر (أحمر)
}

/**
 * نوع حكم التجويد كـ Enum مستقل لسهولة الترجمة والإدارة.
 */
enum class TajweedRuleType(
    val titleKey: String,
    val adviceKey: String,
    val goldenLen: Int
) {
    NORMAL_MADD(
        titleKey = "tajweed_rule_normal_madd",
        adviceKey = "tajweed_advice_normal_madd",
        goldenLen = 2
    ),
    MUTTASEL_MADD(
        titleKey = "tajweed_rule_mottasel_madd",
        adviceKey = "tajweed_advice_mottasel_madd",
        goldenLen = 4
    ),
    MUNFASEL_MADD(
        titleKey = "tajweed_rule_monfasel_madd",
        adviceKey = "tajweed_advice_monfasel_madd",
        goldenLen = 4
    ),
    AARED_MADD(
        titleKey = "tajweed_rule_aared_madd",
        adviceKey = "tajweed_advice_aared_madd",
        goldenLen = 4
    ),
    LAZEM_MADD(
        titleKey = "tajweed_rule_lazem_madd",
        adviceKey = "tajweed_advice_lazem_madd",
        goldenLen = 6
    ),
    LEEN_MADD(
        titleKey = "tajweed_rule_leen_madd",
        adviceKey = "tajweed_advice_leen_madd",
        goldenLen = 2
    ),
    MUSHADDAD_GHUNNAH(
        titleKey = "tajweed_rule_mushaddad_ghunnah",
        adviceKey = "tajweed_advice_mushaddad_ghunnah",
        goldenLen = 2
    ),
    SHADDAH(
        titleKey = "tajweed_rule_shaddah",
        adviceKey = "tajweed_advice_shaddah",
        goldenLen = 1
    );

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

/**
 * شجرة أنواع الأخطاء الصوتية والتجويدية التي يمكن أن يقع فيها القارئ.
 * تحمل المعرفات المترجمة والقيم الرقمية الديناميكية لترجمتها بسلاسة.
 */
sealed class ReciterMistake(
    val severity: ReciterErrorSeverity,
    val adviceKey: String
) {
    /**
     * نقص في زمن المد أو الغنة (حبس الصوت بأقل من المطلوب).
     */
    data class UnderheldDuration(
        val ruleType: TajweedRuleType,
        val actualSeconds: Double,
        val expectedSeconds: Double,
        val goldenHarakat: Int = ruleType.goldenLen
    ) : ReciterMistake(ReciterErrorSeverity.TAJWEED, ruleType.adviceKey)

    /**
     * زيادة مفرطة في زمن المد (تجاوز الحد الأقصى المسموح).
     */
    data class OverheldDuration(
        val ruleType: TajweedRuleType,
        val actualSeconds: Double,
        val maxAllowedSeconds: Double
    ) : ReciterMistake(ReciterErrorSeverity.TAJWEED, "tajweed_advice_overheld")

    /**
     * خطأ في التشكيل (نطق حركة خاطئة مثل فتحة بدل كسرة على نفس الحرف).
     */
    data class TashkeelMismatch(
        val expectedPhoneme: String,
        val actualPhoneme: String
    ) : ReciterMistake(ReciterErrorSeverity.TASHKEEL, "tajweed_advice_tashkeel")

    /**
     * إسقاط حرف (بلع حرف أو نسيانه تماماً).
     */
    data class LetterOmitted(
        val omittedLetter: String
    ) : ReciterMistake(ReciterErrorSeverity.PHONETIC, "tajweed_advice_omission")

    /**
     * إبدال حرف بآخر (نطق حرف مكان آخر).
     */
    data class LetterSubstituted(
        val expectedLetter: String,
        val actualLetter: String
    ) : ReciterMistake(ReciterErrorSeverity.PHONETIC, "tajweed_advice_substitution")
}
