package dev.sayed.mehrabalmomen.data.recitation.tracking

import android.util.Log
import dev.sayed.mehrabalmomen.data.recitation.tajweed.ErrorExplainer
import dev.sayed.mehrabalmomen.data.recitation.tajweed.ReciterMistake
import dev.sayed.mehrabalmomen.presentation.screen.recitation.WordStatus
import kotlin.math.max
import kotlin.math.min

/**
 * Robust Forward Dictation Sequencer with Wasl / Ikhfaa / Idgham Merging Support.
 * Protects evaluated Green/Yellow statuses from being overwritten by Red skip commits.
 */
class DictationSequencer(
    private val onWordEvent: (wordId: Int, status: WordStatus, cleanAsr: String, mistakes: List<ReciterMistake>) -> Unit
) {
    companion object {
        private const val TAG = "Tasmee3Tracker"
    }

    var wordBoundaries = listOf<Int>()
    var fullPhonemes = ""
    var isTajweed = false
    var currentSurahNumber = 0

    var currentSegmentAsrText = ""
    var currentSegmentTimestamps = listOf<Double>()
    var asrCharAnchor = 0
    private var trimmedOffset = 0

    var targetWordCursor = 0
    private val committedStatuses = mutableMapOf<Int, WordStatus>()

    private val matcher = QuranDictationMatcher()
    var config = TrackerConfig(
        enableEarlyMatching = false,
        matchingStrictness = MatchingStrictness.normal
    )

    val wordCount: Int get() = max(0, wordBoundaries.size - 1)

    // ── Pending Madd tail drain ──────────────────────────────────────────────
    // After a word is committed (Green/Yellow), if it ended with a Madd vowel
    // (ا، و، ي), the reciter may hold it for several hundred ms. During this
    // hold, Sherpa keeps emitting the vowel token, which would otherwise get
    // fed into the DTW window of the *next* word and confuse the matcher.
    // pendingTail stores the expected trailing phonemes so syncStream() can
    // absorb them before advancing to the next word's match window.
    private var pendingTail: String? = null

    private val MADD_CHARS = setOf('ا', 'آ', 'و', 'ي', 'ى', 'ۥ', 'ۦ', 'ٰ')

    private fun enqueueTail(refWord: String) {
        val tail = refWord.trimEnd { ch -> ch !in MADD_CHARS }
        pendingTail = if (tail.isNotEmpty() && MADD_CHARS.any { it in tail }) tail else null
    }

    private fun drainPendingTail() {
        val tail = pendingTail ?: return
        if (tail.isEmpty()) { pendingTail = null; return }

        var tailIdx = 0
        while (asrCharAnchor < currentSegmentAsrText.length && tailIdx < tail.length) {
            val asrCode      = currentSegmentAsrText[asrCharAnchor].code
            val expectedCode = tail[tailIdx].code

            when {
                asrCode == expectedCode -> { asrCharAnchor++; tailIdx++ }
                asrCode in MADD_CHARS.map { it.code } && expectedCode in MADD_CHARS.map { it.code } -> {
                    // Absorb prolonged duplicate vowel frames without advancing tailIdx
                    asrCharAnchor++
                }
                else -> {
                    // Reciter has moved on — lift the shield immediately
                    pendingTail = null
                    return
                }
            }
        }
        if (tailIdx >= tail.length) pendingTail = null
    }

    fun setSurahReference(
        phonemes: String,
        boundaries: List<Int>,
        surahNum: Int,
        tajweed: Boolean,
        startWord: Int = 0,
        forceClear: Boolean = true
    ) {
        currentSurahNumber = surahNum
        fullPhonemes = phonemes.replace(" ", "")
        wordBoundaries = boundaries
        isTajweed = tajweed

        committedStatuses.clear()
        asrCharAnchor = 0
        trimmedOffset = 0
        lastReanchorAttemptOffset = -1
        lastReanchorAttemptLen = -1
        pendingTail = null

        if (forceClear) {
            currentSegmentAsrText = ""
            currentSegmentTimestamps = emptyList()
        }

        targetWordCursor = startWord.coerceIn(0, wordCount)
        Log.d(TAG, "📖 [REFERENCE] Surah $surahNum loaded: $wordCount words. Starting at cursor: $targetWordCursor")
    }

    fun syncStream(asrText: String, timestamps: List<Double>, isNewSegment: Boolean) {
        if (isNewSegment || asrText.length < trimmedOffset) {
            currentSegmentAsrText = ""
            currentSegmentTimestamps = emptyList()
            asrCharAnchor = 0
            trimmedOffset = 0
            lastReanchorAttemptOffset = -1
            lastReanchorAttemptLen = -1
            pendingTail = null
        }

        currentSegmentAsrText = if (asrText.length >= trimmedOffset) asrText.substring(trimmedOffset) else asrText
        val tsStart = min(trimmedOffset, timestamps.size)
        currentSegmentTimestamps = if (tsStart < timestamps.size) timestamps.subList(tsStart, timestamps.size) else emptyList()

        processSequence()
    }

    private fun getRefWord(wordId: Int): String {
        if (wordId < 0 || wordId >= wordCount) return ""
        val start = wordBoundaries[wordId]
        val end = if (wordId + 1 < wordBoundaries.size) wordBoundaries[wordId + 1] else fullPhonemes.length
        return fullPhonemes.substring(start, min(end, fullPhonemes.length))
    }

    private fun isWaslMergeCandidate(wordId: Int): Boolean {
        val w = getRefWord(wordId)
        if (w.isEmpty()) return false
        val lastChar = w.last()
        return lastChar == 'ن' || lastChar == 'م' || lastChar == 'ں' || lastChar == 'ۢ' || lastChar == 'ً' || lastChar == 'ٌ' || lastChar == 'ٍ'
    }

    private fun processSequence() {
        val totalWords = wordCount
        if (totalWords == 0) return

        while (asrCharAnchor < currentSegmentAsrText.length && targetWordCursor < totalWords) {
            // ── Drain any prolonged Madd vowels from the previously committed word ──
            drainPendingTail()
            if (pendingTail != null) break  // Still draining — wait for more audio

            val unconsumed = currentSegmentAsrText.substring(asrCharAnchor)
            if (unconsumed.isBlank()) break

            val tsStart = min(asrCharAnchor, currentSegmentTimestamps.size)
            val unconsumedTs = if (tsStart < currentSegmentTimestamps.size) currentSegmentTimestamps.subList(tsStart, currentSegmentTimestamps.size) else emptyList()

            val currentRef = getRefWord(targetWordCursor)
            Log.v(TAG, "🔍 [EVAL] Word #$targetWordCursor (Ref: \"$currentRef\") vs Incoming ASR: \"$unconsumed\"")

            var matched = false
            var waitingForPartial = false

            val dynamicMaxSkip = if (isWaslMergeCandidate(targetWordCursor)) 2 else config.maxSkipWords
            val maxSkip = if (unconsumed.length >= 4) dynamicMaxSkip else 0

            for (skip in 0..maxSkip) {
                val testWordId = targetWordCursor + skip
                if (testWordId >= totalWords) break

                val refStart = wordBoundaries[testWordId]
                val refEnd = if (testWordId + 1 < wordBoundaries.size) wordBoundaries[testWordId + 1] else fullPhonemes.length
                val testRef = fullPhonemes.substring(refStart, min(refEnd, fullPhonemes.length))

                val matchStart = if (skip > 0 && isWaslMergeCandidate(targetWordCursor)) wordBoundaries[targetWordCursor] else refStart

                val result = matcher.matchWord(
                    asrText = unconsumed,
                    asrTimestamps = unconsumedTs,
                    fullPhonemes = fullPhonemes,
                    refStart = matchStart,
                    refEnd = refEnd,
                    config = config,
                    isTajweed = isTajweed
                )

                if (result != null) {
                    if (result.isPartial) {
                        if (skip == 0) {
                            Log.d(TAG, "⏳ [WAITING] Word #$testWordId (\"$testRef\") is currently being spoken... (ASR: \"$unconsumed\")")
                            waitingForPartial = true
                            break
                        } else {
                            continue
                        }
                    }

                    if (result.tokensConsumed > 0) {
                        for (s in 0 until skip) {
                            val skippedWordId = targetWordCursor + s
                            val skippedRef = getRefWord(skippedWordId)
                            if (isWaslMergeCandidate(skippedWordId)) {
                                Log.i(TAG, "🔗 [WASL MERGE] Word #$skippedWordId (\"$skippedRef\") absorbed via Wasl/Ikhfaa -> Marked GREEN")
                                commitWordStatus(skippedWordId, WordStatus.GREEN, "")
                            } else {
                                Log.w(TAG, "🔴 [RESULT: RED] Word #$skippedWordId (\"$skippedRef\") was SKIPPED -> Marked RED")
                                commitWordStatus(skippedWordId, WordStatus.RED, "")
                            }
                        }

                        val mistakes = if (isTajweed) {
                            ErrorExplainer.evaluateWord(
                                wordId = testWordId,
                                fullPhonemes = fullPhonemes,
                                wordBoundaries = wordBoundaries,
                                cleanAsr = result.cleanAsr,
                                timestamps = result.timestamps,
                                alignments = result.trace,
                                speed = config.recitationSpeed,
                                isTajweedEnabled = isTajweed
                            )
                        } else {
                            emptyList()
                        }

                        val finalStatus = when {
                            mistakes.any { it.severity == dev.sayed.mehrabalmomen.data.recitation.tajweed.ReciterErrorSeverity.TAJWEED || it.severity == dev.sayed.mehrabalmomen.data.recitation.tajweed.ReciterErrorSeverity.TASHKEEL } || result.pathCost > 0.15 -> {
                                android.util.Log.i(TAG, "🟡 [RESULT: YELLOW] Word #$testWordId (\"$testRef\") Tajweed/Tashkeel slip (${mistakes.size} mistakes, cost: ${"%.2f".format(result.pathCost)})")
                                WordStatus.YELLOW
                            }
                            else -> {
                                android.util.Log.i(TAG, "🟢 [RESULT: GREEN] Word #$testWordId (\"$testRef\") MATCHED PERFECTLY -> Marked GREEN (cost: ${"%.2f".format(result.pathCost)})")
                                WordStatus.GREEN
                            }
                        }

                        commitWordStatus(testWordId, finalStatus, result.cleanAsr, mistakes)

                        asrCharAnchor += result.tokensConsumed
                        targetWordCursor = testWordId + 1
                        matched = true
                        lastReanchorAttemptOffset = -1
                        lastReanchorAttemptLen = -1

                        // Enqueue tail drain for any trailing Madd vowels in committed word
                        enqueueTail(testRef)
                        break
                    }
                }
            }

            if (waitingForPartial) {
                break
            }

            if (!matched) {
                if (config.enableAutoReanchor && !isTajweed) {
                    val oldCursor = targetWordCursor
                    val reanchored = attemptAutoReanchor()
                    if (reanchored && targetWordCursor != oldCursor) {
                        continue
                    }
                }
                Log.d(TAG, "⏸️ [HOLD] No match at Word #$targetWordCursor (\"$currentRef\"). Holding cursor until speech aligns.")
                break
            }
        }

        val keepCushion = 50
        if (asrCharAnchor > keepCushion + 50) {
            val trim = asrCharAnchor - keepCushion
            trimmedOffset += trim
            currentSegmentAsrText = currentSegmentAsrText.substring(trim)
            currentSegmentTimestamps = if (trim < currentSegmentTimestamps.size) currentSegmentTimestamps.subList(trim, currentSegmentTimestamps.size) else emptyList()
            asrCharAnchor = keepCushion
        }
    }

    private var lastReanchorAttemptOffset = -1
    private var lastReanchorAttemptLen = -1

    private fun attemptAutoReanchor(): Boolean {
        if (!config.enableAutoReanchor || isTajweed) return false
        if (wordBoundaries.isEmpty() || fullPhonemes.isEmpty()) return false

        val unconsumedLen = currentSegmentAsrText.length - asrCharAnchor
        if (unconsumedLen < config.reanchorStallThreshold) return false

        val currentTotalLen = currentSegmentAsrText.length
        if (lastReanchorAttemptOffset == asrCharAnchor && lastReanchorAttemptLen == currentTotalLen) {
            return false
        }
        lastReanchorAttemptOffset = asrCharAnchor
        lastReanchorAttemptLen = currentTotalLen

        val rawQuery = currentSegmentAsrText.substring(asrCharAnchor)

        val fullLen = min(64, rawQuery.length)
        var result = probeQueryWindow(rawQuery.substring(0, fullLen), 0)

        if (result == null && fullLen > 36) {
            result = probeQueryWindow(rawQuery.substring(0, 36), 0)
        }

        if (result == null && rawQuery.length > 28) {
            val tailLen = min(28, rawQuery.length)
            val tailOffset = rawQuery.length - tailLen
            result = probeQueryWindow(rawQuery.substring(tailOffset), tailOffset)
        }

        if (result == null) {
            if (unconsumedLen >= 50) {
                Log.w(TAG, "⚠️ [STALL ESCAPE] Unconsumed speech ($unconsumedLen chars) stalled. Advancing anchor to escape deadlock.")
                asrCharAnchor += min(15, unconsumedLen / 3)
                return true
            }
            return false
        }

        Log.i(TAG, "⚓ [AUTO RE-ANCHOR] Stalled at word $targetWordCursor -> Re-anchoring to word ${result.wordIndex} (dist: ${result.dist}, offset: ${result.probeOffset})")

        if (result.wordIndex > targetWordCursor) {
            for (w in targetWordCursor until result.wordIndex) {
                commitWordStatus(w, WordStatus.RED, "")
            }
        }

        asrCharAnchor += result.probeOffset
        targetWordCursor = result.wordIndex
        return true
    }

    private data class ReanchorResult(val wordIndex: Int, val dist: Int, val probeOffset: Int)

    private fun probeQueryWindow(queryText: String, offset: Int): ReanchorResult? {
        val qLen = queryText.length
        if (qLen < min(18, config.reanchorStallThreshold)) return null

        val maxDist = (qLen * 0.22).toInt()
        val matches = dev.sayed.mehrabalmomen.data.recitation.search.FuzzySearch.findNearMatches(queryText, fullPhonemes, maxDist)
        if (matches.isEmpty()) return null

        data class CandidateWord(val wordIndex: Int, val dist: Int)
        val candidateWords = mutableListOf<CandidateWord>()
        for (m in matches) {
            val w = findWordIndexAtPhonemeOffset(m.start)
            if (w != targetWordCursor && w != targetWordCursor + 1) {
                candidateWords.add(CandidateWord(w, m.dist))
            }
        }
        if (candidateWords.isEmpty()) return null

        candidateWords.sortWith { a, b ->
            if (a.dist != b.dist) {
                a.dist.compareTo(b.dist)
            } else {
                val aForward = a.wordIndex > targetWordCursor
                val bForward = b.wordIndex > targetWordCursor
                if (aForward != bForward) {
                    if (aForward) -1 else 1
                } else {
                    kotlin.math.abs(a.wordIndex - targetWordCursor)
                        .compareTo(kotlin.math.abs(b.wordIndex - targetWordCursor))
                }
            }
        }

        val best = candidateWords[0]

        // Anti-Ambiguity / Mutashabihat Guard:
        // If there is another candidate at a distant verse (> 2 words apart) with a close edit distance (< 3), suppress jump
        for (i in 1 until candidateWords.size) {
            val c = candidateWords[i]
            if (kotlin.math.abs(c.wordIndex - best.wordIndex) > 2 && (c.dist - best.dist) < 3) {
                Log.w(TAG, "⚠️ [RE-ANCHOR AMBIGUITY] Suppressed jump between word ${best.wordIndex} and ${c.wordIndex}")
                return null
            }
        }

        return ReanchorResult(best.wordIndex, best.dist, offset)
    }

    private fun findWordIndexAtPhonemeOffset(charOffset: Int): Int {
        if (wordBoundaries.isEmpty()) return 0
        var low = 0
        var high = wordBoundaries.size - 1
        var found = 0
        while (low <= high) {
            val mid = (low + high) shr 1
            if (wordBoundaries[mid] <= charOffset) {
                found = mid
                low = mid + 1
            } else {
                high = mid - 1
            }
        }
        return found.coerceIn(0, wordCount)
    }

    private fun commitWordStatus(
        wordId: Int,
        status: WordStatus,
        cleanAsr: String,
        mistakes: List<ReciterMistake> = emptyList()
    ) {
        val existing = committedStatuses[wordId]
        // Never overwrite a successfully evaluated Green or Yellow status with Red
        if (existing == WordStatus.GREEN || existing == WordStatus.YELLOW) {
            return
        }
        committedStatuses[wordId] = status
        onWordEvent(wordId, status, cleanAsr, mistakes)
    }
}
