package dev.sayed.mehrabalmomen.data.recitation.tajweed

import dev.sayed.mehrabalmomen.data.recitation.tracking.PhonemeGroupAlignment
import dev.sayed.mehrabalmomen.data.recitation.tracking.PhoneticCostEngine
import dev.sayed.mehrabalmomen.data.recitation.tracking.RecitationSpeed
import kotlin.math.min

private data class PhoneticSpan(
    val refStart: Int,
    val refEnd: Int,
    val refText: String,
    val baseChar: Char,
    val isMadd: Boolean,
    val isShaddah: Boolean,
    val isGhunnah: Boolean,
    val matchedRule: TajweedRule? = null
)

object ErrorExplainer {

    private val VOWELS = setOf('َ', 'ُ', 'ِ', 'ً', 'ٌ', 'ٍ', 'ْ')
    private val MADD_VOWELS = setOf('ا', 'ۦ', 'ۥ')

    fun evaluateWord(
        wordId: Int,
        fullPhonemes: String,
        wordBoundaries: List<Int>,
        cleanAsr: String,
        timestamps: List<Double>,
        alignments: List<PhonemeGroupAlignment>,
        speed: RecitationSpeed = RecitationSpeed.normal,
        isTajweedEnabled: Boolean = true
    ): List<ReciterMistake> {
        if (wordId < 0 || wordId >= wordBoundaries.size - 1) return emptyList()

        val wordRefStart = wordBoundaries[wordId]
        val wordRefEnd = if (wordId + 1 < wordBoundaries.size) wordBoundaries[wordId + 1] else fullPhonemes.length
        if (wordRefStart >= wordRefEnd) return emptyList()

        val spans = buildWordSpans(fullPhonemes, wordRefStart, wordRefEnd)
        val mistakes = mutableListOf<ReciterMistake>()

        for (span in spans) {
            val spanAlignments = alignments.filter { a ->
                a.refIdx >= span.refStart && a.refIdx < span.refEnd
            }
            if (spanAlignments.isEmpty()) continue

            val predBuilder = StringBuilder()
            val usedPredIndices = mutableSetOf<Int>()
            var totalSpanDuration = 0.0

            for (a in spanAlignments) {
                val predIdx = a.predIdx
                if (predIdx in 0 until cleanAsr.length) {
                    predBuilder.append(cleanAsr[predIdx])
                    if (usedPredIndices.add(predIdx)) {
                        if (predIdx < timestamps.size) {
                            totalSpanDuration += timestamps[predIdx]
                        }
                    }
                }
            }

            val predText = predBuilder.toString()
            val spanMistakes = evaluateSpan(
                span = span,
                predText = predText,
                spanDuration = totalSpanDuration,
                wordRefEnd = wordRefEnd,
                speed = speed,
                isTajweedEnabled = isTajweedEnabled
            )
            mistakes.addAll(spanMistakes)
        }

        return mistakes.distinctBy { (it::class.simpleName ?: "") + "_" + (it as? ReciterMistake.UnderheldDuration)?.ruleType + "_" + (it as? ReciterMistake.OverheldDuration)?.ruleType }
    }

    private fun buildWordSpans(
        fullPhonemes: String,
        wordRefStart: Int,
        wordRefEnd: Int
    ): List<PhoneticSpan> {
        val spans = mutableListOf<PhoneticSpan>()
        var cursor = wordRefStart

        while (cursor < wordRefEnd) {
            val ch = fullPhonemes[cursor]

            if (ch in MADD_VOWELS) {
                var end = cursor
                while (end < wordRefEnd && fullPhonemes[end] == ch) {
                    end++
                }
                val refText = fullPhonemes.substring(cursor, end)
                val maddRule = deriveMaddRule(refText.length)

                spans.add(
                    PhoneticSpan(
                        refStart = cursor,
                        refEnd = end,
                        refText = refText,
                        baseChar = ch,
                        isMadd = true,
                        isShaddah = false,
                        isGhunnah = false,
                        matchedRule = maddRule
                    )
                )
                cursor = end
                continue
            }

            if ((ch == 'ن' || ch == 'م') &&
                cursor + 2 < wordRefEnd &&
                fullPhonemes[cursor + 1] == ch &&
                fullPhonemes[cursor + 2] == ch
            ) {
                var end = cursor
                while (end < wordRefEnd && fullPhonemes[end] == ch) {
                    end++
                }
                if (end < wordRefEnd && fullPhonemes[end] in VOWELS) {
                    end++
                }
                val refText = fullPhonemes.substring(cursor, end)
                spans.add(
                    PhoneticSpan(
                        refStart = cursor,
                        refEnd = end,
                        refText = refText,
                        baseChar = ch,
                        isMadd = false,
                        isShaddah = false,
                        isGhunnah = true,
                        matchedRule = MushaddadGhunnahRule()
                    )
                )
                cursor = end
                continue
            }

            if (cursor + 1 < wordRefEnd &&
                fullPhonemes[cursor + 1] == ch &&
                ch !in MADD_VOWELS
            ) {
                var end = cursor
                while (end < wordRefEnd && fullPhonemes[end] == ch) {
                    end++
                }
                if (end < wordRefEnd && fullPhonemes[end] in VOWELS) {
                    end++
                }
                val refText = fullPhonemes.substring(cursor, end)
                spans.add(
                    PhoneticSpan(
                        refStart = cursor,
                        refEnd = end,
                        refText = refText,
                        baseChar = ch,
                        isMadd = false,
                        isShaddah = true,
                        isGhunnah = false,
                        matchedRule = ShaddahRule()
                    )
                )
                cursor = end
                continue
            }

            var end = cursor + 1
            while (end < wordRefEnd && fullPhonemes[end] in VOWELS) {
                end++
            }
            val refText = fullPhonemes.substring(cursor, end)
            spans.add(
                PhoneticSpan(
                    refStart = cursor,
                    refEnd = end,
                    refText = refText,
                    baseChar = ch,
                    isMadd = false,
                    isShaddah = false,
                    isGhunnah = false
                )
            )
            cursor = end
        }

        return spans
    }

    private fun evaluateSpan(
        span: PhoneticSpan,
        predText: String,
        spanDuration: Double,
        wordRefEnd: Int,
        speed: RecitationSpeed,
        isTajweedEnabled: Boolean
    ): List<ReciterMistake> {
        val mistakes = mutableListOf<ReciterMistake>()

        if (span.refText.isNotEmpty()) {
            if (predText.isEmpty()) {
                if (isTajweedEnabled && (span.isMadd || span.isShaddah || span.isGhunnah)) {
                    val rule = span.matchedRule ?: ShaddahRule()
                    mistakes.add(
                        ReciterMistake.UnderheldDuration(
                            ruleType = TajweedRuleType.fromRule(rule),
                            actualSeconds = 0.0,
                            expectedSeconds = rule.getRequiredDuration(speed)
                        )
                    )
                } else {
                    mistakes.add(ReciterMistake.LetterOmitted(span.refText))
                }
                return mistakes
            } else if (span.baseChar != predText[0] &&
                !PhoneticCostEngine.isEquivalentGlyph(span.baseChar, predText[0])
            ) {
                mistakes.add(
                    ReciterMistake.LetterSubstituted(
                        expectedLetter = span.refText,
                        actualLetter = predText
                    )
                )
                return mistakes
            }
        }

        if (isTajweedEnabled && (span.isMadd || span.isGhunnah || span.isShaddah)) {
            if (spanDuration > 0.0 && span.matchedRule != null) {
                val rule = span.matchedRule
                val durStatus = rule.checkDurationStatus(spanDuration, speed)
                val ruleType = TajweedRuleType.fromRule(rule)

                if (durStatus == TajweedDurationStatus.underheld) {
                    mistakes.add(
                        ReciterMistake.UnderheldDuration(
                            ruleType = ruleType,
                            actualSeconds = spanDuration,
                            expectedSeconds = rule.getRequiredDuration(speed),
                            goldenHarakat = rule.goldenLen.toInt()
                        )
                    )
                    return mistakes
                } else if (durStatus == TajweedDurationStatus.overheld) {
                    mistakes.add(
                        ReciterMistake.OverheldDuration(
                            ruleType = ruleType,
                            actualSeconds = spanDuration,
                            maxAllowedSeconds = rule.getMaxAllowedDuration(speed)
                        )
                    )
                    return mistakes
                }
            }
        }

        val refVowels = extractVowels(span.refText)
        val predVowels = extractVowels(predText)

        if (refVowels.isNotEmpty() || predVowels.isNotEmpty()) {
            val isTerminalWaqf = span.refEnd == wordRefEnd && predVowels.isEmpty()
            if (!isTerminalWaqf && refVowels != predVowels) {
                mistakes.add(
                    ReciterMistake.TashkeelMismatch(
                        expectedPhoneme = span.refText,
                        actualPhoneme = predText
                    )
                )
            }
        }

        return mistakes
    }

    private fun deriveMaddRule(length: Int): TajweedRule = when {
        length >= 6 -> LazemMaddRule()
        length >= 4 -> MottaselMaddRule(4)
        else -> NormalMaddRule()
    }

    private fun extractVowels(text: String): String =
        text.filter { it in VOWELS }
}
