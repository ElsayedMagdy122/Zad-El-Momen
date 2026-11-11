package dev.sayed.mehrabalmomen.data.recitation.tracking

import kotlin.math.max

data class OmissionResult(
    val isOmissionDetected: Boolean,
    val omittedWordIndex: Int?,
    val shortfall: Int,
    val confidenceGap: Int,
    val scoreRatio: Double
)

/**
 * Dynamic-programming Longest Common Subsequence (LCS) omission locator
 * based on the Best-Drop algorithm from the `tasmee3-muaalem-findings` benchmark.
 */
class LcsOmissionDetector {
    companion object {
        fun lcsLength(s1: String, s2: String): Int {
            val n = s1.length
            val m = s2.length
            if (n == 0 || m == 0) return 0

            var previousRow = IntArray(m + 1)
            var currentRow = IntArray(m + 1)

            for (i in n - 1 downTo 0) {
                val c1 = s1[i]
                for (j in m - 1 downTo 0) {
                    if (c1 == s2[j]) {
                        currentRow[j] = previousRow[j + 1] + 1
                    } else {
                        currentRow[j] = max(previousRow[j], previousRow[j + 1])
                    }
                }
                val temp = previousRow
                previousRow = currentRow
                currentRow = temp
            }

            return previousRow[0]
        }

        fun detectOmission(
            phonemesPerWord: List<String>,
            emittedPhonemes: String
        ): OmissionResult {
            if (phonemesPerWord.isEmpty()) {
                return OmissionResult(false, null, 0, 0, 0.0)
            }

            val emittedLen = emittedPhonemes.length
            val refFull = phonemesPerWord.joinToString("")
            val refLen = refFull.length

            if (emittedLen == 0) {
                return OmissionResult(true, 0, refLen, 0, 0.0)
            }

            val lcsFull = lcsLength(refFull, emittedPhonemes)
            val shortfall = refLen - lcsFull

            if (shortfall <= 0) {
                return OmissionResult(false, null, shortfall, 0, lcsFull.toDouble() / emittedLen)
            }

            val scores = mutableListOf<Int>()
            val refWithoutLengths = mutableListOf<Int>()

            for (wi in phonemesPerWord.indices) {
                val sb = StringBuilder()
                for (i in phonemesPerWord.indices) {
                    if (i != wi) sb.append(phonemesPerWord[i])
                }
                val refWithout = sb.toString()
                refWithoutLengths.add(refWithout.length)
                scores.add(lcsLength(refWithout, emittedPhonemes))
            }

            var allEqualRefWithout = true
            for (i in scores.indices) {
                if (scores[i] != refWithoutLengths[i]) {
                    allEqualRefWithout = false
                    break
                }
            }

            if (allEqualRefWithout && scores.size > 1) {
                return OmissionResult(false, null, shortfall, 0, (scores.firstOrNull() ?: 0).toDouble() / emittedLen)
            }

            var maxScore = -1
            var bestIndex = -1
            for (i in scores.indices) {
                if (scores[i] > maxScore) {
                    maxScore = scores[i]
                    bestIndex = i
                }
            }

            val sortedScores = scores.sortedDescending()
            val top1 = sortedScores.firstOrNull() ?: 0
            val top2 = if (sortedScores.size > 1) sortedScores[1] else 0
            val gap = top1 - top2

            return OmissionResult(
                isOmissionDetected = bestIndex >= 0,
                omittedWordIndex = if (bestIndex >= 0) bestIndex else null,
                shortfall = shortfall,
                confidenceGap = gap,
                scoreRatio = if (emittedLen > 0) maxScore.toDouble() / emittedLen else 0.0
            )
        }
    }
}
