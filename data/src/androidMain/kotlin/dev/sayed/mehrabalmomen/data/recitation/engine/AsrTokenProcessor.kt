package dev.sayed.mehrabalmomen.data.recitation.engine

import kotlin.math.max
import kotlin.math.min

data class ProcessedAudioStream(
    val tokens: List<String>,
    val durations: List<Double>,
    val starts: List<Double>,
    val ends: List<Double>,
    val spikeTimes: List<Double>
) {
    val isEmpty: Boolean get() = tokens.isEmpty()
    val isNotEmpty: Boolean get() = tokens.isNotEmpty()
    val text: String get() = tokens.joinToString("")

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

class AsrTokenProcessor(
    private var maxTokenDuration: Double = 2.5
) {
    companion object {
        fun isMaddToken(tok: String): Boolean {
            if (tok.isEmpty()) return false
            if (tok.all { c -> c in '\u064B'..'\u0652' }) return false
            if (isShaddahToken(tok) || isGhunnahToken(tok)) return false
            return tok.any { c ->
                c == 'ا' || c == 'آ' || c == 'و' || c == 'ي' || c == 'ى' ||
                c == 'ٰ' || c == 'ٱ' || c == 'ۥ' || c == 'ۦ' || c == 'ٲ'
            }
        }

        fun isGhunnahToken(tok: String): Boolean =
            tok.startsWith("مم") || tok.startsWith("نن") ||
            tok.contains('ں') || tok.contains('۾')

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

    fun process(result: TranscriptionResult): ProcessedAudioStream {
        val maxCount = min(result.tokens.size, result.timestamps.size)

        var commonLen = 0
        val minLen = min(lastRawTokens.size, maxCount)
        for (i in 0 until minLen) {
            if (lastRawTokens[i] == result.tokens[i]) commonLen++ else break
        }

        if (commonLen < lastRawTokens.size) {
            reset()
            commonLen = 0
        }

        lastRawTokens.clear()
        lastRawTokens.addAll(result.tokens.take(maxCount))

        for (i in commonLen until maxCount) {
            val tok   = result.tokens[i]
            val rawTs = result.timestamps[i]

            if (tok.isEmpty() || tok in BLANK_TOKENS) continue

            filteredTokens.add(tok)
            filteredSpikeTimes.add(rawTs)
        }

        recomputeDurations()

        return ProcessedAudioStream(
            tokens     = filteredTokens.toList(),
            durations  = tokenDurations.toList(),
            starts     = tokenStarts.toList(),
            ends       = tokenEnds.toList(),
            spikeTimes = filteredSpikeTimes.toList()
        )
    }

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
