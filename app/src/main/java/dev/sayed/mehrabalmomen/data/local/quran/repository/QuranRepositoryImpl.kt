package dev.sayed.mehrabalmomen.data.local.quran.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.sayed.mehrabalmomen.data.local.quran.dto.AyahDto
import dev.sayed.mehrabalmomen.data.local.quran.mappers.toDomain
import dev.sayed.mehrabalmomen.domain.entity.Ayah
import dev.sayed.mehrabalmomen.domain.entity.Surah
import dev.sayed.mehrabalmomen.domain.repository.QuranRepository

class QuranRepositoryImpl(private val context: Context, private val gson: Gson) : QuranRepository {

    // https://cdn.jsdelivr.net/npm/quran-json@3.1.2/dist/quran_en.json
    /*
    *  edit quran file to support items and enhance performance
    * */
    override suspend fun getAyahs(surahNumber: Int): List<Ayah> {
        return getAllAyahsFromAsset()
            .filter { it.surahNumber == surahNumber }
            .map { it.toDomain() }
    }

    override suspend fun getSurahs(): List<Surah> {
        return getAllAyahsFromAsset()
            .distinctBy { it.surahNumber }
            .map { dto ->
                Surah(
                    surahNumber = dto.surahNumber,
                    nameArabic = dto.surahNameAr,
                    nameEnglish = dto.surahNameEn,
                    ayahCount = getAyahs(dto.surahNumber).last().ayahNumber,
                    type = Surah.SurahType.MAKKI
                )
            }
    }

    private fun getAllAyahsFromAsset(): List<AyahDto> {
        return try {
            val jsonString = context.assets.open("hafsData_v2-0.json")
                .bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<AyahDto>>() {}.type
            gson.fromJson(jsonString, listType)
        } catch (e: Exception) {
            emptyList()
        }
    }
}