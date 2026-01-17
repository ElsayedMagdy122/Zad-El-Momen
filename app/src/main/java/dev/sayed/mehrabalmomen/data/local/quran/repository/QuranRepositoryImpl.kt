package dev.sayed.mehrabalmomen.data.local.quran.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.sayed.mehrabalmomen.data.local.quran.dto.AyahDto
import dev.sayed.mehrabalmomen.data.local.quran.dto.SurahDto
import dev.sayed.mehrabalmomen.data.local.quran.mappers.toDomain
import dev.sayed.mehrabalmomen.domain.entity.Ayah
import dev.sayed.mehrabalmomen.domain.entity.Surah
import dev.sayed.mehrabalmomen.domain.repository.QuranRepository

class QuranRepositoryImpl(private val context: Context, private val gson: Gson) : QuranRepository {

    // https://cdn.jsdelivr.net/npm/quran-json@3.1.2/dist/quran_en.json
    /*
    *  edit quran file to support items and enhance performance
    * */
    private fun getAllSurahsFromAsset(): List<SurahDto> {
        return try {
            val jsonString = context.assets.open("quran_structured.json")
                .bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<SurahDto>>() {}.type
            gson.fromJson(jsonString, listType)
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
}