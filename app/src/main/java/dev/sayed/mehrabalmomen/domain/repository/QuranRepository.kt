package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.entity.quran.Ayah
import dev.sayed.mehrabalmomen.domain.entity.quran.Surah

interface QuranRepository {
    suspend fun getSurahs(): List<Surah>
    suspend fun getAyahs(surahNumber: Int): List<Ayah>
    suspend fun searchInQuran(query: String): List<Ayah>
    suspend fun searchInSurah(surahNumber: Int, query: String): List<Ayah>
    suspend fun getAyahTafseer(surahNumber: Int, ayahNumber: Int): String
}