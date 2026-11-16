package dev.sayed.mehrabalmomen.domain.model.recitation

enum class ReciterErrorSeverity {
    TAJWEED,
    TASHKEEL,
    PHONETIC
}

sealed interface ReciterMistake {
    val severity: ReciterErrorSeverity
    val advice: String

    data class UnderheldDuration(
        val ruleName: String,
        val actualSeconds: Double,
        val expectedSeconds: Double,
        override val advice: String
    ) : ReciterMistake {
        override val severity = ReciterErrorSeverity.TAJWEED
    }

    data class OverheldDuration(
        val ruleName: String,
        val actualSeconds: Double,
        val maxAllowedSeconds: Double,
        override val advice: String
    ) : ReciterMistake {
        override val severity = ReciterErrorSeverity.TAJWEED
    }

    data class TashkeelMismatch(
        val expectedPhoneme: String,
        val actualPhoneme: String,
        override val advice: String
    ) : ReciterMistake {
        override val severity = ReciterErrorSeverity.TASHKEEL
    }

    data class LetterOmitted(
        val omittedLetter: String,
        override val advice: String
    ) : ReciterMistake {
        override val severity = ReciterErrorSeverity.PHONETIC
    }

    data class LetterSubstituted(
        val expectedLetter: String,
        val actualLetter: String,
        override val advice: String
    ) : ReciterMistake {
        override val severity = ReciterErrorSeverity.PHONETIC
    }
}
