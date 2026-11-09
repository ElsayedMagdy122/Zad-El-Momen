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

        // 1. Handle Repetition (if repeat is active, check near end of current ayah)
        if (repeatCount > 0 && currentPositionMs >= currentTiming.endTimeMs - 250) {
            if (currentRepeatIteration < repeatCount - 1) {
                return NextAction.RepeatAyah(currentRepeatIteration + 1, currentTiming.startTimeMs)
            }
        }

        // 2. If continuous reading is OFF (!isContinuousReading), play current ayah to its end and stop
        if (!isContinuousReading) {
            if (currentPositionMs >= currentTiming.endTimeMs) {
                return NextAction.StopAtAyah(currentTiming.startTimeMs)
            }
            return NextAction.None
        }

        // 3. If continuous reading is ON, check if we reached or passed the end of the current ayah
        if (currentPositionMs >= currentTiming.endTimeMs - 250) {
            if (currentAyahId >= totalAyatCount) {
                return NextAction.FinishPlayback
            }
            if (currentPositionMs >= currentTiming.endTimeMs) {
                val nextAyahId = currentAyahId + 1
                if (nextAyahId <= totalAyatCount) {
                    return NextAction.MoveToAyah(nextAyahId)
                }
            }
        }

        // 4. Check for natural progression to next ayah in continuous mode
        val activeTiming = timings.find { currentPositionMs >= it.startTimeMs && currentPositionMs < it.endTimeMs }
        if (activeTiming != null && activeTiming.verseNumber != currentAyahId) {
            return NextAction.MoveToAyah(activeTiming.verseNumber)
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
