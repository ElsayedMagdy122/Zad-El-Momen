package dev.sayed.mehrabalmomen.data.recitation.tracking

import android.util.Log
import dev.sayed.mehrabalmomen.presentation.recitation.WordStatus
import kotlin.math.max
import kotlin.math.min

/**
 * Robust Forward Dictation Sequencer.
 * Evaluates recitation word-by-word with comprehensive diagnostic logs (Ref vs Heard vs Color).
 */
class DictationSequencer(
    private val onWordEvent: (wordId: Int, status: WordStatus, cleanAsr: String) -> Unit
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

        if (forceClear) {
            currentSegmentAsrText = ""
            currentSegmentTimestamps = emptyList()
        }

        targetWordCursor = startWord.coerceIn(0, wordCount)
        Log.d(TAG, "📖 [REFERENCE] Surah $surahNum loaded: $wordCount words. Starting at cursor: $targetWordCursor")
        if (!forceClear && currentSegmentAsrText.isNotEmpty()) {
            processSequence()
        }
    }

    fun syncStream(asrText: String, timestamps: List<Double>, isNewSegment: Boolean) {
        if (isNewSegment || asrText.length < trimmedOffset) {
            currentSegmentAsrText = ""
            currentSegmentTimestamps = emptyList()
            asrCharAnchor = 0
            trimmedOffset = 0
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

    private fun processSequence() {
        val totalWords = wordCount
        if (totalWords == 0) return

        while (asrCharAnchor < currentSegmentAsrText.length && targetWordCursor < totalWords) {
            val unconsumed = currentSegmentAsrText.substring(asrCharAnchor)
            if (unconsumed.isBlank()) break

            val tsStart = min(asrCharAnchor, currentSegmentTimestamps.size)
            val unconsumedTs = if (tsStart < currentSegmentTimestamps.size) currentSegmentTimestamps.subList(tsStart, currentSegmentTimestamps.size) else emptyList()

            val currentRef = getRefWord(targetWordCursor)
            Log.v(TAG, "🔍 [EVAL] Word #$targetWordCursor (Ref: \"$currentRef\") vs Incoming ASR: \"$unconsumed\"")

            var matched = false
            var waitingForPartial = false

            // Try current word (skip = 0), then lookahead (skip = 1, 2)
            val maxSkip = if (unconsumed.length >= 4) config.maxSkipWords else 0

            for (skip in 0..maxSkip) {
                val testWordId = targetWordCursor + skip
                if (testWordId >= totalWords) break

                val refStart = wordBoundaries[testWordId]
                val refEnd = if (testWordId + 1 < wordBoundaries.size) wordBoundaries[testWordId + 1] else fullPhonemes.length
                val testRef = fullPhonemes.substring(refStart, min(refEnd, fullPhonemes.length))

                val result = matcher.matchWord(
                    asrText = unconsumed,
                    asrTimestamps = unconsumedTs,
                    fullPhonemes = fullPhonemes,
                    refStart = refStart,
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
                        // 1. Mark skipped / mispronounced prior words as RED
                        for (s in 0 until skip) {
                            val skippedWordId = targetWordCursor + s
                            val skippedRef = getRefWord(skippedWordId)
                            Log.w(TAG, "🔴 [RESULT: RED] Word #$skippedWordId (\"$skippedRef\") was SKIPPED or MISPRONOUNCED -> Marked RED")
                            commitWordStatus(skippedWordId, WordStatus.RED, "")
                        }

                        // 2. Decide status for the matched word
                        val finalStatus = if (isTajweed && result.hasTajweedError) {
                            Log.i(TAG, "🟡 [RESULT: YELLOW] Word #$testWordId (\"$testRef\") pronounced correctly but with Tajweed slip -> Marked YELLOW (cost: ${"%.2f".format(result.pathCost)}, heard: \"${result.cleanAsr}\")")
                            WordStatus.YELLOW
                        } else {
                            Log.i(TAG, "🟢 [RESULT: GREEN] Word #$testWordId (\"$testRef\") MATCHED PERFECTLY -> Marked GREEN (cost: ${"%.2f".format(result.pathCost)}, heard: \"${result.cleanAsr}\")")
                            WordStatus.GREEN
                        }

                        commitWordStatus(testWordId, finalStatus, result.cleanAsr)

                        asrCharAnchor += result.tokensConsumed
                        targetWordCursor = testWordId + 1
                        matched = true
                        break
                    }
                }
            }

            if (waitingForPartial) {
                break
            }

            if (!matched) {
                // If user has spoken substantial audio (e.g. 8+ characters) that doesn't match current word
                // and doesn't match future words, but reciter is advancing
                Log.d(TAG, "⏸️ [HOLD] No match at Word #$targetWordCursor (\"$currentRef\"). Holding cursor until speech aligns.")
                break
            }
        }

        // Sliding window head-trimming
        val keepCushion = 30
        if (asrCharAnchor > keepCushion + 30) {
            val trim = asrCharAnchor - keepCushion
            trimmedOffset += trim
            currentSegmentAsrText = currentSegmentAsrText.substring(trim)
            currentSegmentTimestamps = if (trim < currentSegmentTimestamps.size) currentSegmentTimestamps.subList(trim, currentSegmentTimestamps.size) else emptyList()
            asrCharAnchor = keepCushion
        }
    }

    private fun commitWordStatus(wordId: Int, status: WordStatus, cleanAsr: String) {
        if (committedStatuses.containsKey(wordId) && committedStatuses[wordId] == WordStatus.GREEN) {
            return
        }
        committedStatuses[wordId] = status
        onWordEvent(wordId, status, cleanAsr)
    }
}
