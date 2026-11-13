package dev.sayed.mehrabalmomen.domain.model.recitation

enum class ReciterErrorSeverity {
    TAJWEED,
    TASHKEEL,
    PHONETIC
}

enum class TajweedRuleType(
    val titleKey: String,
    val adviceKey: String,
    val goldenLen: Int
) {
    NORMAL_MADD("tajweed_rule_normal_madd", "tajweed_advice_normal_madd", 2),
    MUTTASEL_MADD("tajweed_rule_mottasel_madd", "tajweed_advice_mottasel_madd", 4),
    MUNFASEL_MADD("tajweed_rule_monfasel_madd", "tajweed_advice_monfasel_madd", 4),
    AARED_MADD("tajweed_rule_aared_madd", "tajweed_advice_aared_madd", 4),
    LAZEM_MADD("tajweed_rule_lazem_madd", "tajweed_advice_lazem_madd", 6),
    LEEN_MADD("tajweed_rule_leen_madd", "tajweed_advice_leen_madd", 2),
    MUSHADDAD_GHUNNAH("tajweed_rule_mushaddad_ghunnah", "tajweed_advice_mushaddad_ghunnah", 2),
    SHADDAH("tajweed_rule_shaddah", "tajweed_advice_shaddah", 1)
}

sealed class ReciterMistake(
    val severity: ReciterErrorSeverity,
    val adviceKey: String
) {
    data class UnderheldDuration(
        val ruleType: TajweedRuleType,
        val actualSeconds: Double,
        val expectedSeconds: Double,
        val goldenHarakat: Int = ruleType.goldenLen
    ) : ReciterMistake(ReciterErrorSeverity.TAJWEED, ruleType.adviceKey)

    data class OverheldDuration(
        val ruleType: TajweedRuleType,
        val actualSeconds: Double,
        val maxAllowedSeconds: Double
    ) : ReciterMistake(ReciterErrorSeverity.TAJWEED, "tajweed_advice_overheld")

    data class TashkeelMismatch(
        val expectedPhoneme: String,
        val actualPhoneme: String
    ) : ReciterMistake(ReciterErrorSeverity.TASHKEEL, "tajweed_advice_tashkeel")

    data class LetterOmitted(
        val omittedLetter: String
    ) : ReciterMistake(ReciterErrorSeverity.PHONETIC, "tajweed_advice_omission")

    data class LetterSubstituted(
        val expectedLetter: String,
        val actualLetter: String
    ) : ReciterMistake(ReciterErrorSeverity.PHONETIC, "tajweed_advice_substitution")
}
