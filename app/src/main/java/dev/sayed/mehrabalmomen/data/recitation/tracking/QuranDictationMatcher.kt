package dev.sayed.mehrabalmomen.data.recitation.tracking

import android.util.Log
import kotlin.math.max
import kotlin.math.min

data class WordMatchResult(
    val pathCost: Double,
    val tokensConsumed: Int,
    val cleanAsr: String,
    val timestamps: List<Double>,
    val trace: List<PhonemeGroupAlignment>,
    val isPartial: Boolean = false,
    val hasTajweedError: Boolean = false
)

data class PhonemeGroupAlignment(
    val opType: String,
    val refIdx: Int,
    val predIdx: Int
)

enum class RecitationSpeed { fast, normal, slow }
enum class MatchingStrictness {
    easy, normal, hard;
    // Calibrated to accept authentic pronunciation variations while strictly rejecting incorrect words
    val defaultMaxPathCost: Double get() = when (this) { easy -> 0.32; normal -> 0.26; hard -> 0.20 }
    val shortWordPathCost: Double get() = when (this) { easy -> 0.26; normal -> 0.22; hard -> 0.18 }
    val mediumWordPathCost: Double get() = when (this) { easy -> 0.28; normal -> 0.24; hard -> 0.19 }
    val maxSkipWords: Int get() = when (this) { easy -> 2; normal -> 2; hard -> 1 }
    val acousticConfusionCost: Double get() = when (this) { easy -> 0.15; normal -> 0.22; hard -> 0.30 }
    val standardInsertionCost: Double get() = when (this) { easy -> 0.60; normal -> 0.75; hard -> 0.90 }
    val standardDeletionCost: Double get() = when (this) { easy -> 0.75; normal -> 0.85; hard -> 1.00 }
}

data class TrackerConfig(
    val recitationSpeed: RecitationSpeed = RecitationSpeed.normal,
    val matchingStrictness: MatchingStrictness = MatchingStrictness.normal,
    val enableEarlyMatching: Boolean = false,
    val enableAutoReanchor: Boolean = true,
    val reanchorStallThreshold: Int = 24
) {
    val defaultMaxPathCost: Double get() = matchingStrictness.defaultMaxPathCost
    val shortWordPathCost: Double get() = matchingStrictness.shortWordPathCost
    val mediumWordPathCost: Double get() = matchingStrictness.mediumWordPathCost
    val maxSkipWords: Int get() = matchingStrictness.maxSkipWords
    val acousticConfusionCost: Double get() = matchingStrictness.acousticConfusionCost
    val standardInsertionCost: Double get() = matchingStrictness.standardInsertionCost
    val standardDeletionCost: Double get() = matchingStrictness.standardDeletionCost
}

/**
 * Semi-Global DTW Matcher for Quranic Recitation.
 * Features Free Start alignment, calibrated thresholds, and diagnostic logging.
 */
class QuranDictationMatcher {
    companion object {
        private const val TAG = "QuranDictationMatcher"
    }

    private var dp = DoubleArray(4096)
    private var bt = ByteArray(4096)

    fun matchWord(
        asrText: String,
        asrTimestamps: List<Double>,
        fullPhonemes: String,
        refStart: Int,
        refEnd: Int,
        config: TrackerConfig = TrackerConfig(),
        isTajweed: Boolean = false
    ): WordMatchResult? {
        val m = asrText.length
        val n = refEnd - refStart
        if (m == 0 || n <= 0) return null

        val stride = n + 1
        val cells = (m + 1) * stride
        if (dp.size < cells) {
            val sz = max(cells, dp.size * 2)
            dp = DoubleArray(sz)
            bt = ByteArray(sz)
        }

        // Row 0: Reference deletions
        dp[0] = 0.0
        bt[0] = 0
        for (j in 1..n) {
            val delCost = PhoneticCostEngine.getDeletionCost(
                fullPhonemes,
                refStart + j - 1,
                config.standardDeletionCost,
                config.acousticConfusionCost
            )
            dp[j] = dp[j - 1] + delCost
            bt[j] = 1
        }

        // Column 0: Free Start (Semi-Global DTW)
        // Allows matching even if leading breath, noise, or acoustic burst precedes the word
        for (i in 1..m) {
            dp[i * stride] = 0.0
            bt[i * stride] = 2
        }

        // Core DP Fill
        for (i in 1..m) {
            val aCode = asrText[i - 1]
            val row = i * stride
            val prev = (i - 1) * stride
            val insCost = PhoneticCostEngine.getInsertionCost(
                asrText,
                i - 1,
                config.standardInsertionCost,
                config.acousticConfusionCost
            )

            for (j in 1..n) {
                val rRef = refStart + j - 1
                val rCode = fullPhonemes[rRef]

                val subCost = PhoneticCostEngine.getSubstitutionCost(aCode, rCode, config.acousticConfusionCost)
                val delCost = PhoneticCostEngine.getDeletionCost(fullPhonemes, rRef, config.standardDeletionCost, config.acousticConfusionCost)

                val sub = dp[prev + j - 1] + subCost
                val del = dp[row + j - 1] + delCost
                val ins = dp[prev + j] + insCost

                if (sub <= del && sub <= ins) {
                    dp[row + j] = sub
                    bt[row + j] = 0 // match/sub
                } else if (del <= ins) {
                    dp[row + j] = del
                    bt[row + j] = 1 // delete
                } else {
                    dp[row + j] = ins
                    bt[row + j] = 2 // insert
                }
            }
        }

        // Effective reference length (excluding non-phonetic markers)
        var effN = 0
        for (j in 0 until n) {
            val code = fullPhonemes[refStart + j]
            if (code == '\u0640' || code == '\u0686' || code == '\u06DC' || code == '\u0619' || code == '\u06EA') continue
            effN++
        }
        if (effN < 1) effN = 1

        val threshold = when {
            effN <= 3 -> config.shortWordPathCost
            effN <= 6 -> config.mediumWordPathCost
            else -> config.defaultMaxPathCost
        }

        var bestI = -1
        var bestCost = Double.POSITIVE_INFINITY

        // Must consume reasonable speech (at least half the word's phonetic length)
        val minTokensNeeded = max(1, (effN * 0.50).toInt())

        for (i in 1..m) {
            val norm = dp[i * stride + n] / effN
            if (norm <= threshold && i >= minTokensNeeded) {
                if (norm < bestCost) {
                    bestI = i
                    bestCost = norm
                }
            }
        }

        // Partial Detection: check if user is mid-word
        var isPartial = false
        if (bestI > 0 && bestI == m) {
            var curJ = n
            while (curJ > 0 && bt[bestI * stride + curJ].toInt() == 1) {
                val rCode = fullPhonemes[refStart + curJ - 1]
                if (!PhoneticCostEngine.isTashkeel(rCode)) {
                    isPartial = true
                    break
                }
                curJ--
            }
        } else if (bestI < 0) {
            val minPrefix = min(n, 2)
            for (j in minPrefix until n) {
                if (m <= j && dp[m * stride + j] / j <= threshold) {
                    isPartial = true
                    break
                }
            }
        }

        if (isPartial) {
            return WordMatchResult(
                pathCost = 0.0,
                tokensConsumed = 0,
                cleanAsr = "",
                timestamps = emptyList(),
                trace = emptyList(),
                isPartial = true
            )
        }

        if (bestI < 0) {
            return null
        }

        // Traceback & Verification
        var ci = bestI
        var cj = n
        val rawTrace = mutableListOf<PhonemeGroupAlignment>()
        val ts = mutableListOf<Double>()
        var matchCount = 0

        while (cj > 0) {
            if (ci == 0) {
                rawTrace.add(PhonemeGroupAlignment("delete", refStart + cj - 1, -1))
                cj--
                continue
            }

            val op = bt[ci * stride + cj].toInt()
            val gRef = refStart + cj - 1

            if (op == 0) {
                val asrCode = asrText[ci - 1]
                val refCode = fullPhonemes[gRef]
                val isMatch = PhoneticCostEngine.getSubstitutionCost(asrCode, refCode) == 0.0
                if (isMatch) matchCount++
                rawTrace.add(PhonemeGroupAlignment(if (isMatch) "match" else "replace", gRef, ci - 1))
                if (ci - 1 < asrTimestamps.size) ts.add(asrTimestamps[ci - 1])
                ci--
                cj--
            } else if (op == 1) {
                rawTrace.add(PhonemeGroupAlignment("delete", gRef, -1))
                cj--
            } else {
                rawTrace.add(PhonemeGroupAlignment("insert", gRef, ci - 1))
                ci--
            }
        }

        // Tajweed error detection (minor acoustic duration or prolongation slip)
        val hasTajweedSlip = isTajweed && (bestCost > 0.15)

        return WordMatchResult(
            pathCost = bestCost,
            tokensConsumed = bestI,
            cleanAsr = asrText.substring(0, bestI),
            timestamps = ts.reversed(),
            trace = rawTrace.reversed(),
            isPartial = false,
            hasTajweedError = hasTajweedSlip
        )
    }
}
