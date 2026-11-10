package dev.sayed.mehrabalmomen.data.recitation.tracking

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
        enableEarlyMatching = true,
        matchingStrictness = MatchingStrictness.normal
    )

    val wordCount: Int get() = max(0, wordBoundaries.size - 1)

    // ── Pending Madd tail drain ──────────────────────────────────────────────
    // After a word is committed early, reserve trailing unuttered phonemes
    // so upcoming words cannot match against leftovers.
    private var pendingTail: String? = null

    private fun drainPendingTail() {
        if (!config.enableEarlyMatching) {
            pendingTail = null
            return
        }
        val tail = pendingTail ?: return
        if (tail.isEmpty()) { pendingTail = null; return }

        var tailIdx = 0
        while (asrCharAnchor < currentSegmentAsrText.length && tailIdx < tail.length) {
            val asrCode = currentSegmentAsrText[asrCharAnchor]
            val expectedCode = tail[tailIdx]

            when {
                asrCode == expectedCode -> { asrCharAnchor++; tailIdx++ }
                PhoneticCostEngine.isMaddVowel(expectedCode) && PhoneticCostEngine.isMaddVowel(asrCode) -> {
                    // Absorb prolonged duplicate vowel frames without advancing tailIdx
                    asrCharAnchor++
                }
                else -> {
                    // Reciter has moved on to next word — lift shield immediately
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

    private fun processSequence() {
        val totalWords = wordCount
        if (totalWords == 0) return

        while (asrCharAnchor < currentSegmentAsrText.length && targetWordCursor < totalWords) {
            // Drain unuttered Madd/tail phonemes before matching next word
            if (config.enableEarlyMatching && !isTajweed && pendingTail != null) {
                drainPendingTail()
                if (pendingTail != null) break
            }

            val unconsumed = currentSegmentAsrText.substring(asrCharAnchor)
            if (unconsumed.isBlank()) break

            val tsStart = min(asrCharAnchor, currentSegmentTimestamps.size)
            val unconsumedTs = if (tsStart < currentSegmentTimestamps.size) {
                currentSegmentTimestamps.subList(tsStart, currentSegmentTimestamps.size)
            } else {
                emptyList()
            }

            var matched = false
            var waitingForPartial = false

            val maxSkip = min(config.maxSkipWords, totalWords - 1 - targetWordCursor)

            for (skip in 0..maxSkip) {
                val startW = targetWordCursor + skip

                for (merge in 1..2) {
                    val endW = startW + merge - 1
                    if (endW >= totalWords) break

                    val refStart = wordBoundaries[startW]
                    val refEnd = if (endW + 1 < wordBoundaries.size) wordBoundaries[endW + 1] else fullPhonemes.length

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
                                waitingForPartial = true
                                break
                            } else {
                                continue
                            }
                        }

                        if (result.tokensConsumed > 0) {
                            if (merge > 1 && !isValidMerge(result, startW, endW, unconsumed)) {
                                continue
                            }

                            // 1. Commit skipped words RED
                            for (s in 0 until skip) {
                                val skippedId = targetWordCursor + s
                                commitWordStatus(skippedId, WordStatus.RED, "")
                            }

                            // 2. Evaluate Tajweed mistakes if Tajweed mode is active
                            val mistakes = if (isTajweed && result.trace.isNotEmpty()) {
                                ErrorExplainer.evaluateWord(
                                    wordId = startW,
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
                                isTajweed && (mistakes.isNotEmpty() || result.hasTajweedError) -> WordStatus.YELLOW
                                else -> WordStatus.GREEN
                            }

                            // Commit matched/merged words
                            for (m in 0 until merge) {
                                val w = startW + m
                                commitWordStatus(w, finalStatus, result.cleanAsr, mistakes)
                            }

                            asrCharAnchor += result.tokensConsumed
                            targetWordCursor = endW + 1
                            matched = true
                            lastReanchorAttemptOffset = -1
                            lastReanchorAttemptLen = -1

                            // Tail reservation for early matching
                            if (config.enableEarlyMatching && !isTajweed) {
                                val wordRefEnd = if (endW + 1 < wordBoundaries.size) wordBoundaries[endW + 1] else fullPhonemes.length
                                var lastMatchedRef = -1
                                for (k in result.trace.size - 1 downTo 0) {
                                    if (result.trace[k].opType != "delete") {
                                        lastMatchedRef = result.trace[k].refIdx
                                        break
                                    }
                                }
                                if (lastMatchedRef != -1 && lastMatchedRef < wordRefEnd - 1) {
                                    pendingTail = fullPhonemes.substring(lastMatchedRef + 1, wordRefEnd)
                                    drainPendingTail()
                                } else {
                                    pendingTail = null
                                }
                            }
                            break
                        }
                    }
                }
                if (matched || waitingForPartial) break
            }

            if (!matched) {
                if (config.enableAutoReanchor && !isTajweed) {
                    val oldCursor = targetWordCursor
                    val reanchored = attemptAutoReanchor()
                    if (reanchored && targetWordCursor != oldCursor) {
                        continue
                    }
                }
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

    private fun isValidMerge(
        result: WordMatchResult,
        startW: Int,
        endW: Int,
        asrText: String
    ): Boolean {
        if (startW == endW) return true

        for (w in startW..endW) {
            val refStart = wordBoundaries[w]
            val refEnd = if (w + 1 < wordBoundaries.size) wordBoundaries[w + 1] else fullPhonemes.length
            val wordLen = refEnd - refStart

            val forgiveStart = if (w > startW) min(2, wordLen / 3) else 0
            val forgiveEnd = if (w < endW) min(2, wordLen / 3) else 0

            val coreStart = refStart + forgiveStart
            val coreEnd = refEnd - forgiveEnd
            val coreLen = coreEnd - coreStart

            if (coreLen <= 0) continue

            var coreCost = 0.0

            for (align in result.trace) {
                if (align.refIdx in coreStart until coreEnd) {
                    when (align.opType) {
                        "delete" -> coreCost += config.standardDeletionCost
                        "replace" -> {
                            if (align.predIdx in asrText.indices && align.refIdx in fullPhonemes.indices) {
                                val asrCode = asrText[align.predIdx]
                                val refCode = fullPhonemes[align.refIdx]
                                coreCost += PhoneticCostEngine.getSubstitutionCost(asrCode, refCode)
                            } else {
                                coreCost += config.standardInsertionCost
                            }
                        }
                    }
                }
            }

            if ((coreCost / coreLen) > config.defaultMaxPathCost) {
                return false
            }
        }
        return true
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
                asrCharAnchor += min(15, unconsumedLen / 3)
                return true
            }
            return false
        }

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
