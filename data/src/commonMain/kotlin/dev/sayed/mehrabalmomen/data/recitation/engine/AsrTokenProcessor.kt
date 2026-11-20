package dev.sayed.mehrabalmomen.data.recitation.engine

import kotlin.math.max
import kotlin.math.min

/**
 * Per-char duration struct emitted for downstream alignment.
 */
data class ProcessedAudioStream(
    val tokens: List<String>,
    val durations: List<Double>,   // per-token durations (seconds)
    val starts: List<Double>,
    val ends: List<Double>,
    val spikeTimes: List<Double>   // per-token peak timestamps
) {
    val isEmpty: Boolean get() = tokens.isEmpty()
    val isNotEmpty: Boolean get() = tokens.isNotEmpty()
    val text: String get() = tokens.joinToString("")

    /**
     * Per-CHARACTER duration list (each char of each multi-char token gets an equal share).
     * This is what the DictationSequencer consumes.
     */
    val charDurations: List<Double>
        get() {
            val result = mutableListOf<Double>()
            for (i in tokens.indices) {
                val tok = tokens[i]
                val dur = durations[i] / max(1, tok.length)
                repeat(tok.length) { result.add(dur) }
            }
            return result
        }
}

// ─────────────────────────────────────────────────────────────────────────────
// ASR TOKEN PROCESSOR
//
// Ingests the raw CTC token list + spike-time list from SherpaEngine.
// Responsibilities:
//   1. Filter blank / special tokens ("<blank>", "<blk>", "<eps>", "eps").
//   2. Detect prefix-overlapping updates (CTC is cumulative per utterance).
//   3. Re-compute per-token acoustic durations using pause-gap heuristics,
//      matching the reference implementation in codeflutter.md exactly.
// ─────────────────────────────────────────────────────────────────────────────
class AsrTokenProcessor(
    private var maxTokenDuration: Double = 2.5
) {
    companion object {
        /** True when the token represents a prolonged Madd (vowel extension). */
        fun isMaddToken(tok: String): Boolean {
            if (tok.isEmpty()) return false
            // Skip pure diacritics
            if (tok.all { c -> c in '\u064B'..'\u0652' }) return false
            if (isShaddahToken(tok) || isGhunnahToken(tok)) return false
            return tok.any { c ->
                c == 'ا' || c == 'آ' || c == 'و' || c == 'ي' || c == 'ى' ||
                c == 'ٰ' || c == 'ٱ' || c == 'ۥ' || c == 'ۦ' || c == 'ٲ'
            }
        }

        /** True when the token represents a Ghunnah (nasalisation hold: نّ / مّ). */
        fun isGhunnahToken(tok: String): Boolean =
            tok.startsWith("مم") || tok.startsWith("نن") ||
            tok.contains('ں') || tok.contains('۾')

        /**
         * True when the token represents a doubled consonant (Shaddah).
         * A doubled consonant is any non-Alef, non-Waaw/Yaa 2-char repetition
         * of an Arabic base letter.
         */
        fun isShaddahToken(tok: String): Boolean {
            if (tok.length < 2) return false
            val c0 = tok[0]
            val c1 = tok[1]
            if (c0 != c1) return false
            if (c0 == 'ا') return false
            if ((c0 == 'و' || c0 == 'ي') && tok.length == 2) return false
            return c0 in '\u0621'..'\u064A'
        }

        fun isElasticContinuant(tok: String): Boolean =
            isMaddToken(tok) || isShaddahToken(tok) || isGhunnahToken(tok)

        private val BLANK_TOKENS = setOf("<blank>", "<blk>", "<eps>", "eps", "▁")
    }

    // ── Incremental state (accumulates across the utterance) ──
    private val lastRawTokens      = mutableListOf<String>()
    private val filteredTokens     = mutableListOf<String>()
    private val filteredSpikeTimes = mutableListOf<Double>()
    private val tokenDurations     = mutableListOf<Double>()
    private val tokenStarts        = mutableListOf<Double>()
    private val tokenEnds          = mutableListOf<Double>()

    fun reset() {
        lastRawTokens.clear()
        filteredTokens.clear()
        filteredSpikeTimes.clear()
        tokenDurations.clear()
        tokenStarts.clear()
        tokenEnds.clear()
    }

    /**
     * Process a new TranscriptionResult from SherpaEngine.
     *
     * CTC models accumulate tokens across the utterance; each new result is
     * a prefix-extended version of the previous one. We find the longest common
     * prefix with the previous result and only append new tokens.
     *
     * If the new token list is *shorter* than the previous one (reset / new
     * segment), we wipe state and start fresh.
     */
    fun process(result: dev.sayed.mehrabalmomen.data.recitation.engine.TranscriptionResult): ProcessedAudioStream {
        val maxCount = min(result.tokens.size, result.timestamps.size)

        // ── 1. Find common prefix with previous emission ──
        var commonLen = 0
        val minLen = min(lastRawTokens.size, maxCount)
        for (i in 0 until minLen) {
            if (lastRawTokens[i] == result.tokens[i]) commonLen++ else break
        }

        // If prefix shrank (new segment / reset), wipe everything
        if (commonLen < lastRawTokens.size) {
            reset()
            commonLen = 0
        }

        // Update rolling snapshot
        lastRawTokens.clear()
        lastRawTokens.addAll(result.tokens.take(maxCount))

        // ── 2. Append genuinely new tokens, skipping blanks ──
        for (i in commonLen until maxCount) {
            val tok   = result.tokens[i]
            val rawTs = result.timestamps[i]

            if (tok.isEmpty() || tok in BLANK_TOKENS) continue

            filteredTokens.add(tok)
            filteredSpikeTimes.add(rawTs)
        }

        // ── 3. Re-derive durations for the whole accumulated list ──
        recomputeDurations()

        return ProcessedAudioStream(
            tokens     = filteredTokens.toList(),
            durations  = tokenDurations.toList(),
            starts     = tokenStarts.toList(),
            ends       = tokenEnds.toList(),
            spikeTimes = filteredSpikeTimes.toList()
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Duration derivation — mirrors the reference implementation exactly.
    //
    // Each token's "spike time" is its CTC peak (the moment it fires).
    // We estimate an onset by looking at the inter-spike gap to the previous
    // token and applying elastic vs. non-elastic thresholds.
    // ─────────────────────────────────────────────────────────────────────────
    private fun recomputeDurations() {
        val count = filteredTokens.size
        tokenDurations.clear()
        tokenStarts.clear()
        tokenEnds.clear()
        if (count == 0) return

        val starts = DoubleArray(count)
        val ends   = DoubleArray(count)
        val durs   = DoubleArray(count)

        for (k in 0 until count) {
            val peak = filteredSpikeTimes[k]
            ends[k]  = peak

            if (k == 0) {
                starts[k] = max(0.0, peak - 0.16)
            } else {
                val peakPrev = filteredSpikeTimes[k - 1]
                val gap      = peak - peakPrev

                // A large inter-spike gap means a pause / boundary between words.
                // Elastic tokens (Madd, Shaddah, Ghunnah) need a wider pause
                // threshold so their extended hold isn't split into two words.
                val pauseThreshold = when {
                    isElasticContinuant(filteredTokens[k])                           -> 3.50
                    isElasticContinuant(filteredTokens[k - 1])
                        || filteredTokens[k - 1].length >= 2                        -> 2.00
                    else                                                              -> 1.25
                }

                starts[k] = if (gap > pauseThreshold) {
                    val onsetWindow = if (isElasticContinuant(filteredTokens[k])) 0.35 else 0.16
                    max(peakPrev + 0.04, peak - onsetWindow)
                } else {
                    peakPrev
                }
            }

            durs[k] = (ends[k] - starts[k]).coerceIn(0.04, maxTokenDuration)
        }

        tokenStarts.addAll(starts.toList())
        tokenEnds.addAll(ends.toList())
        tokenDurations.addAll(durs.toList())
    }
}
