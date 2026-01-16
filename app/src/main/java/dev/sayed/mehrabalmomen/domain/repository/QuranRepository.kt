package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.entity.Ayah
import dev.sayed.mehrabalmomen.domain.entity.Surah

interface QuranRepository {
    suspend fun getSurahs(): List<Surah>
    suspend fun getAyahs(surahNumber: Int): List<Ayah>
}