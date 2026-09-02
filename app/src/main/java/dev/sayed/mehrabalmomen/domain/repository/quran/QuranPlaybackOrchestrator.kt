package dev.sayed.mehrabalmomen.domain.repository.quran

import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioVerseTiming

/**
 * Domain component responsible for managing the logic of Quran playback,
 * including repetitions and verse transitions.
 */
class QuranPlaybackOrchestrator {

    /**
     * Determines the next action based on current playback position and settings.
     */
    fun calculateNextPlaybackAction(
        currentPositionMs: Long,
        currentAyahId: Int,
        timings: List<QuranAudioVerseTiming>,
        repeatCount: Int,
        currentRepeatIteration: Int,
        isContinuousReading: Boolean,
        totalAyatCount: Int
    ): NextAction {
        val currentTiming = timings.find { it.verseNumber == currentAyahId } ?: return NextAction.None

        // Check if we reached the end of the current ayah (with a small buffer)
        if (currentPositionMs >= currentTiming.endTimeMs - 250) {
            
            // Handle Repetition
            if (repeatCount > 0 && currentRepeatIteration < repeatCount - 1) {
                return NextAction.RepeatAyah(currentRepeatIteration + 1, currentTiming.startTimeMs)
            }
            
            // Handle Continuity
            if (!isContinuousReading) {
                return NextAction.StopAtAyah(currentTiming.startTimeMs)
            }
            
            // Check for end of Surah
            if (currentAyahId >= totalAyatCount) {
                return NextAction.FinishPlayback
            }
            
            return NextAction.None // Let it flow to the next ayah naturally if continuous
        }

        // Detect if the player naturally moved to a new ayah (from the stream)
        val detectedTiming = timings.find { currentPositionMs >= it.startTimeMs && currentPositionMs < it.endTimeMs }
        if (detectedTiming != null && detectedTiming.verseNumber != currentAyahId) {
            return NextAction.MoveToAyah(detectedTiming.verseNumber)
        }

        return NextAction.None
    }

    sealed class NextAction {
        object None : NextAction()
        data class RepeatAyah(val nextIteration: Int, val startTimeMs: Long) : NextAction()
        data class StopAtAyah(val startTimeMs: Long) : NextAction()
        data class MoveToAyah(val ayahId: Int) : NextAction()
        object FinishPlayback : NextAction()
    }
}
