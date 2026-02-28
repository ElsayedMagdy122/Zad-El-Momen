package dev.sayed.mehrabalmomen.data.local.quran.repository

import android.content.Context
import dev.sayed.mehrabalmomen.data.local.quran.dto.SurahDto
import dev.sayed.mehrabalmomen.data.local.quran.dto.TafseerDto
import dev.sayed.mehrabalmomen.data.local.quran.mappers.toDomain
import dev.sayed.mehrabalmomen.domain.entity.quran.Ayah
import dev.sayed.mehrabalmomen.domain.entity.quran.Surah
import dev.sayed.mehrabalmomen.domain.repository.QuranRepository
import kotlinx.serialization.json.Json

class QuranRepositoryImpl(private val context: Context, private val json: Json) : QuranRepository {

    private fun getAllSurahsFromAsset(): List<SurahDto> {
        return try {
            val jsonString = context.assets.open("quran_structured.json")
                .bufferedReader()
                .use { it.readText() }

            json.decodeFromString<List<SurahDto>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun getAllTafseerFromAsset(): List<TafseerDto> {
        return try {
            val jsonString = context.assets.open("tafseer.json")
                .bufferedReader()
                .use { it.readText() }

            json.decodeFromString<List<TafseerDto>>(jsonString)
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getSurahs(): List<Surah> {
        return getAllSurahsFromAsset().map { dto ->
            Surah(
                surahNumber = dto.id,
                nameArabic = dto.nameArabic,
                nameEnglish = dto.nameEnglish,
                ayahCount = dto.totalVerses,
                type = if (dto.type == "meccan") Surah.SurahType.MAKKI else Surah.SurahType.MADANI
            )
        }
    }

    override suspend fun getAyahs(surahNumber: Int): List<Ayah> {
        val surah = getAllSurahsFromAsset().find { it.id == surahNumber }
        return surah?.verses?.map { ayahDto ->
            ayahDto.toDomain(surahNumber)
        } ?: emptyList()
    }

    override suspend fun searchInQuran(query: String): List<Ayah> {
        val allSurahs = getAllSurahsFromAsset()
        val results = mutableListOf<Ayah>()

        allSurahs.forEach { surahDto ->
            val matchingAyahs = surahDto.verses.filter { ayahDto ->
                ayahDto.textEmlaey.contains(query, ignoreCase = true)
            }.map { it.toDomain(surahDto.id, surahDto.nameArabic, surahDto.nameEnglish) }

            results.addAll(matchingAyahs)
        }
        return results
    }

    override suspend fun searchInSurah(surahNumber: Int, query: String): List<Ayah> {
        val surah = getAllSurahsFromAsset().find { it.id == surahNumber }
        return surah?.verses?.filter { ayahDto ->
            ayahDto.textEmlaey.contains(query, ignoreCase = true)
        }?.map { it.toDomain(surahNumber) } ?: emptyList()
    }

    override suspend fun getAyahTafseer(surahNumber: Int, ayahNumber: Int): String {
        val allTafseer = getAllTafseerFromAsset()
        return allTafseer.find {
            it.surahNumber.toInt() == surahNumber && it.ayahNumber.toInt() == ayahNumber
        }?.text ?: ""
    }
}