package dev.sayed.mehrabalmomen.data.recitation.data

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

data class QuranVerse(
    val surah: Int,
    val ayah: Int,
    val textUthmani: String,
    val phonemeWords: List<String>,
    val displayWords: List<String>,
    val globalStartIndex: Int = 0
)

class QuranRepository(
    private val jsonReader: suspend (String) -> String
) {
    companion object {
        private val BRACES_REGEX = Regex("\\{[^}]*\\}")
        private val WHITESPACE_REGEX = Regex("\\s+")
    }

    private var versesMap: Map<String, kotlinx.serialization.json.JsonElement>? = null

    suspend fun loadData() {
        if (versesMap != null) return
        try {
            val jsonString = jsonReader("ordered_quran_phonemes.json")
            val element = Json.parseToJsonElement(jsonString)
            val versesObj = element.jsonObject["verses"]?.jsonObject ?: element.jsonObject
            versesMap = versesObj
        } catch (_: Exception) {
        }
    }

    suspend fun getVersesForSurah(surah: Int): List<QuranVerse> {
        loadData()
        val verses = versesMap ?: return emptyList()

        val list = mutableListOf<QuranVerse>()
        var runningGlobalWordIndex = 0

        for (a in 1..286) {
            val key = "$surah:$a"
            val verseObj = verses[key]?.jsonObject ?: break

            val uthmani = verseObj["aya_text"]?.jsonPrimitive?.content
                ?: verseObj["uthmani"]?.jsonPrimitive?.content
                ?: verseObj["aya_ui"]?.jsonPrimitive?.content
                ?: ""

            val phonemesList = mutableListOf<String>()
            val pArray = verseObj["aya_phonemes_list"]?.jsonArray ?: verseObj["phoneme_words"]?.jsonArray
            if (pArray != null && pArray.isNotEmpty()) {
                for (item in pArray) {
                    val p = item.jsonPrimitive.content.trim().replace("{", "").replace("}", "")
                    if (p.isNotEmpty()) phonemesList.add(p)
                }
            }

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

        return list
    }

    suspend fun getVerse(surah: Int, ayah: Int): QuranVerse? {
        val surahList = getVersesForSurah(surah)
        return surahList.getOrNull(ayah - 1)
    }
}
