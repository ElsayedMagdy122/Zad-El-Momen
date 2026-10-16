package dev.sayed.mehrabalmomen.data.recitation.data

import android.content.Context
import android.util.Log
import org.json.JSONObject

data class QuranVerse(
    val surah: Int,
    val ayah: Int,
    val textUthmani: String,
    val phonemeWords: List<String>,
    val displayWords: List<String>,
    val globalStartIndex: Int = 0
)

/**
 * Repository for parsing and providing Quranic verses and phoneme references from `ordered_quran_phonemes.json`.
 * Ensures 1:1 mapping between phonetic tokens and UI display words to guarantee exact word alignment.
 */
class QuranRepository(private val context: Context) {
    companion object {
        private const val TAG = "QuranRepository"
    }

    private var versesObj: JSONObject? = null
    private val surahCache = mutableMapOf<Int, List<QuranVerse>>()

    @Synchronized
    fun loadData() {
        if (versesObj != null) return
        try {
            Log.d(TAG, "📖 [QuranRepository] Loading ordered_quran_phonemes.json...")
            val jsonString = context.assets.open("ordered_quran_phonemes.json").bufferedReader(Charsets.UTF_8).use {
                it.readText()
            }
            val root = JSONObject(jsonString)
            versesObj = root.optJSONObject("verses") ?: root
            Log.d(TAG, "✅ [QuranRepository] Successfully loaded ordered_quran_phonemes.json (${versesObj?.length() ?: 0} verses)")
        } catch (e: Exception) {
            Log.e(TAG, "❌ [QuranRepository] Error loading ordered_quran_phonemes.json: ${e.message}", e)
        }
    }

    @Synchronized
    fun getVersesForSurah(surah: Int): List<QuranVerse> {
        surahCache[surah]?.let { return it }
        loadData()
        val verses = versesObj ?: return emptyList()

        val list = mutableListOf<QuranVerse>()
        var runningGlobalWordIndex = 0

        for (a in 1..286) {
            val key = "$surah:$a"
            val verseObj = verses.optJSONObject(key) ?: break

            val uthmani = verseObj.optString("aya_text", verseObj.optString("uthmani", verseObj.optString("aya_ui", "")))

            val phonemesList = mutableListOf<String>()
            val pList = verseObj.optJSONArray("aya_phonemes_list") ?: verseObj.optJSONArray("phoneme_words")
            if (pList != null && pList.length() > 0) {
                for (i in 0 until pList.length()) {
                    val p = pList.optString(i, "").trim()
                    if (p.isNotEmpty()) phonemesList.add(p)
                }
            }

            // Clean display words from Uthmani text
            val rawWords = uthmani.trim().split("\\s+".toRegex()).filter { it.isNotBlank() }

            val finalDisplayWords: List<String>
            val finalPhonemeWords: List<String>

            if (phonemesList.isNotEmpty()) {
                finalPhonemeWords = phonemesList
                // Align display words to match phoneme list size if possible
                if (rawWords.size == phonemesList.size) {
                    finalDisplayWords = rawWords
                } else if (rawWords.isNotEmpty()) {
                    finalDisplayWords = rawWords
                } else {
                    finalDisplayWords = phonemesList
                }
            } else {
                finalPhonemeWords = rawWords
                finalDisplayWords = rawWords
            }

            list.add(
                QuranVerse(
                    surah = surah,
                    ayah = a,
                    textUthmani = uthmani,
                    phonemeWords = finalPhonemeWords,
                    displayWords = finalDisplayWords,
                    globalStartIndex = runningGlobalWordIndex
                )
            )

            runningGlobalWordIndex += finalPhonemeWords.size
        }

        surahCache[surah] = list
        return list
    }

    fun getVerse(surah: Int, ayah: Int): QuranVerse? {
        val surahList = getVersesForSurah(surah)
        return surahList.getOrNull(ayah - 1)
    }
}
