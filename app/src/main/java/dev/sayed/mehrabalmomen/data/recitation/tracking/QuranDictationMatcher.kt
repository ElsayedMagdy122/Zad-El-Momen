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
    val defaultMaxPathCost: Double get() = when (this) { easy -> 0.40; normal -> 0.30; hard -> 0.25 }
    val shortWordPathCost: Double get() = when (this) { easy -> 0.30; normal -> 0.25; hard -> 0.20 }
    val mediumWordPathCost: Double get() = when (this) { easy -> 0.35; normal -> 0.28; hard -> 0.23 }
    val maxSkipWords: Int get() = when (this) { easy -> 1; normal -> 2; hard -> 3 }
    val acousticConfusionCost: Double get() = when (this) { easy -> 0.15; normal -> 0.25; hard -> 0.35 }
    val standardInsertionCost: Double get() = when (this) { easy -> 0.50; normal -> 0.75; hard -> 1.00 }
    val standardDeletionCost: Double get() = when (this) { easy -> 0.80; normal -> 1.00; hard -> 1.00 }
    val maxTokenDurationAllowed: Double get() = when (this) { easy -> 3.0; normal -> 2.5; hard -> 2.0 }
}

data class TrackerConfig(
    val recitationSpeed: RecitationSpeed = RecitationSpeed.normal,
    val matchingStrictness: MatchingStrictness = MatchingStrictness.normal,
    val enableEarlyMatching: Boolean = true,
    val enableAutoReanchor: Boolean = false,
    val reanchorStallThreshold: Int = 24
) {
    val defaultMaxPathCost: Double get() = matchingStrictness.defaultMaxPathCost
    val shortWordPathCost: Double get() = matchingStrictness.shortWordPathCost
    val mediumWordPathCost: Double get() = matchingStrictness.mediumWordPathCost
    val maxSkipWords: Int get() = matchingStrictness.maxSkipWords
    val acousticConfusionCost: Double get() = matchingStrictness.acousticConfusionCost
    val standardInsertionCost: Double get() = matchingStrictness.standardInsertionCost
    val standardDeletionCost: Double get() = matchingStrictness.standardDeletionCost
    val maxTokenDurationAllowed: Double get() = matchingStrictness.maxTokenDurationAllowed
}

/**
 * Semi-Global DTW Matcher for Quranic Recitation.
 * Features Free Start alignment, calibrated thresholds, and diagnostic logging.
 * Matches Flutter reference implementation with zero-allocation optimizations.
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

        // Core DP Fill with tie-breaking in favor of deletion (matching Flutter)
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

                // Use sub < del instead of sub <= del to break ties in favor of deletions
                if (sub < del && sub <= ins) {
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

        // Effective reference length: collapse consecutive identical Madd vowels and skip zero-cost markers
        var effN = 0
        for (j in 0 until n) {
            val code = fullPhonemes[refStart + j]
            if (PhoneticCostEngine.isZeroCostMarker(code)) continue
            if (j > 0 && code == fullPhonemes[refStart + j - 1] && PhoneticCostEngine.isMaddVowel(code)) {
                continue
            }
            effN++
        }
        if (effN < 1) effN = 1

        var threshold = config.defaultMaxPathCost
        if (effN <= 3) {
            threshold = min(threshold, config.shortWordPathCost)
        } else if (effN <= 7) {
            threshold = min(threshold, config.mediumWordPathCost)
        } else {
            threshold = min(threshold, config.defaultMaxPathCost)
        }

        var bestI = -1
        var bestCost = Double.POSITIVE_INFINITY

        val allowEarlyBreak = config.enableEarlyMatching && !isTajweed && effN >= 4
        for (i in 1..m) {
            val norm = dp[i * stride + n] / effN
            if (norm <= threshold) {
                if (if (allowEarlyBreak) norm < bestCost else norm <= bestCost) {
                    bestI = i
                    bestCost = norm
                    if (allowEarlyBreak && bestCost == 0.0) break
                }
            }
        }

        // Partial Detection: Strict frontier rule matching Flutter
        var isPartial = false
        if (isTajweed && bestI > 0 && bestI == m) {
            var curJ = n
            var hasCoreConsonantMissing = false

            while (curJ > 0 && bt[bestI * stride + curJ].toInt() == 1) {
                val rCode = fullPhonemes[refStart + curJ - 1]
                val isRepeated = curJ > 1 && rCode == fullPhonemes[refStart + curJ - 2]

                if (!PhoneticCostEngine.isTashkeel(rCode) &&
                    !PhoneticCostEngine.isZeroCostMarker(rCode) &&
                    !isRepeated
                ) {
                    hasCoreConsonantMissing = true
                    break
                }
                curJ--
            }

            if (hasCoreConsonantMissing) {
                isPartial = true
            } else if (curJ > 0 && bt[bestI * stride + curJ].toInt() == 0) {
                val asrCode = asrText[bestI - 1]
                val refCode = fullPhonemes[refStart + curJ - 1]

                if (PhoneticCostEngine.isTashkeel(asrCode) && PhoneticCostEngine.isTashkeel(refCode)) {
                    isPartial = false
                } else if (PhoneticCostEngine.getSubstitutionCost(asrCode, refCode) > 0.0) {
                    isPartial = true
                }
            }
        } else if (bestI < 0) {
            val minJ = if (n > 2) 2 else 1
            val startI = max(1, m - 2)
            for (i in startI..m) {
                for (j in minJ until n) {
                    if (dp[i * stride + j] / j <= threshold) {
                        isPartial = true
                        break
                    }
                }
                if (isPartial) break
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
