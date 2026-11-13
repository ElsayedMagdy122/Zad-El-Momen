package dev.sayed.mehrabalmomen.data.recitation.search

import kotlin.math.max

data class FuzzyMatch(
    val start: Int,
    val end: Int,
    val dist: Int
)

/**
 * Gene Myers' 64-bit Bit-Parallel Substring Search algorithm for fuzzy phonetic matching.
 */
class FuzzySearch {
    companion object {
        fun findNearMatches(query: String, text: String, maxDist: Int): List<FuzzyMatch> {
            val n = query.length
            val m = text.length
            if (n == 0 || m == 0 || maxDist < 0) return emptyList()

            if (n <= 64) {
                return bitParallelSearch(query, text, maxDist)
            }
            return emptyList()
        }

        private fun bitParallelSearch(query: String, text: String, maxDist: Int): List<FuzzyMatch> {
            val n = query.length
            val m = text.length
            val queryUnits = query.codePoints().toArray()
            val textUnits = text.codePoints().toArray()

            val charMask = LongArray(2048)
            for (i in 0 until n) {
                val code = queryUnits[i]
                if (code < 2048) {
                    charMask[code] = charMask[code] or (1L shl i)
                }
            }

            val fullMask = (1L shl n) - 1L
            val topMask = 1L shl (n - 1)

            var vp = fullMask
            var vn = 0L
            var currDist = n
            val rawMatches = mutableListOf<FuzzyMatch>()

            for (j in 0 until m) {
                val code = textUnits[j]
                val pm = if (code < 2048) charMask[code] else 0L

                val x = pm or vn
                val d0 = (((pm and vp) + vp) xor vp) or x
                val hn = vp and d0
                var hp = vn or ((vp or d0).inv() and fullMask)

                if ((hp and topMask) != 0L) currDist++
                if ((hn and topMask) != 0L) currDist--

                hp = (hp shl 1) and fullMask
                val hnShifted = (hn shl 1) and fullMask
                vp = (hnShifted or ((d0 or hp).inv() and fullMask)) and fullMask
                vn = hp and d0

                if (currDist <= maxDist) {
                    val matchEnd = j + 1
                    rawMatches.add(
                        FuzzyMatch(
                            start = max(0, matchEnd - n),
                            end = matchEnd,
                            dist = currDist
                        )
                    )
                }
            }

            return filterOverlapping(rawMatches)
        }

        private fun filterOverlapping(matches: List<FuzzyMatch>): List<FuzzyMatch> {
            if (matches.isEmpty()) return emptyList()
            val sorted = matches.sortedWith(compareBy({ it.start }, { it.end }, { it.dist }))
            val filtered = mutableListOf<FuzzyMatch>()
            var current = sorted[0]

            for (i in 1 until sorted.size) {
                val next = sorted[i]
                if (next.start < current.end) {
                    if (next.dist < current.dist) {
                        current = next
                    }
                } else {
                    filtered.add(current)
                    current = next
                }
            }
            filtered.add(current)
            return filtered
        }
    }
}
