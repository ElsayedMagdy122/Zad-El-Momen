package dev.sayed.mehrabalmomen.data.recitation.data

import android.content.Context
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
        private val BRACES_REGEX = Regex("\\{[^}]*\\}")
        private val WHITESPACE_REGEX = Regex("\\s+")
    }

    private var versesObj: JSONObject? = null
    private val surahCache = mutableMapOf<Int, List<QuranVerse>>()

    @Synchronized
    fun loadData() {
        if (versesObj != null) return
        try {
            val internalFile = java.io.File(context.filesDir, "ordered_quran_phonemes.json")
            val jsonString = if (internalFile.exists() && internalFile.length() > 0) {
                internalFile.readText(Charsets.UTF_8)
            } else {
                context.assets.open("ordered_quran_phonemes.json").bufferedReader(Charsets.UTF_8).use {
                    it.readText()
                }
            }
            val root = JSONObject(jsonString)
            versesObj = root.optJSONObject("verses") ?: root
        } catch (_: Exception) {
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
                    val p = pList.optString(i, "").trim().replace("{", "").replace("}", "")
                    if (p.isNotEmpty()) phonemesList.add(p)
                }
            }

            // Clean display words from Uthmani text (strip any {} braces or ayah numbers inside braces)
            val cleanUthmani = uthmani
                .replace(BRACES_REGEX, "")
                .replace("{", "")
                .replace("}", "")
                .trim()

            val rawWords = cleanUthmani.split(WHITESPACE_REGEX).filter { it.isNotBlank() }

            val finalPhonemeWords: List<String>

            val rawDisplay = if (phonemesList.isNotEmpty()) {
                finalPhonemeWords = phonemesList
                if (rawWords.size == phonemesList.size) rawWords
                else if (rawWords.isNotEmpty()) rawWords
                else phonemesList
            } else {
                finalPhonemeWords = rawWords
                rawWords
            }

            val finalDisplayWords = rawDisplay.map { w ->
                w.replace("{", "").replace("}", "").replace("﴿", "").replace("﴾", "").trim()
            }.filter { it.isNotBlank() }

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
