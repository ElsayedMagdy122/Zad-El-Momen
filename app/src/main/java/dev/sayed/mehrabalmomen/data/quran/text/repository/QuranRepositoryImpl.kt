package dev.sayed.mehrabalmomen.data.quran.text.repository

import android.content.Context
import dev.sayed.mehrabalmomen.data.quran.text.local.dto.SurahDto
import dev.sayed.mehrabalmomen.data.quran.text.local.dto.TafseerDto
import dev.sayed.mehrabalmomen.data.quran.text.local.mappers.toDomain
import dev.sayed.mehrabalmomen.domain.entity.quran.text.Ayah
import dev.sayed.mehrabalmomen.domain.entity.quran.text.Surah
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranRepository
import dev.sayed.mehrabalmomen.domain.repository.settings.SettingsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

class QuranRepositoryImpl(
    private val context: Context,
    private val json: Json,
    private val settingsRepository: SettingsRepository
) : QuranRepository {

    // Thread-safe in-memory cache for surahs
    private var cachedSurahs: List<SurahDto>? = null
    private var surahsMap: Map<Int, SurahDto>? = null
    private val surahsMutex = Mutex()

    // Thread-safe in-memory cache for tafseer
    private var cachedTafseerFileName: String? = null
    private var cachedTafseer: List<TafseerDto>? = null
    private val tafseerMutex = Mutex()

    private suspend fun getAllSurahsFromAsset(): List<SurahDto> {
        cachedSurahs?.let { return it }
        return surahsMutex.withLock {
            cachedSurahs ?: try {
                val jsonString = context.assets.open("quran_structured.json")
                    .bufferedReader()
                    .use { it.readText() }

                json.decodeFromString<List<SurahDto>>(jsonString).also {
                    cachedSurahs = it
                    surahsMap = it.associateBy { surah -> surah.id }
                }
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    private suspend fun getAllTafseerFromAsset(): List<TafseerDto> {
        val fileName = settingsRepository.observeTafseer().first()
        if (cachedTafseerFileName == fileName && cachedTafseer != null) {
            return cachedTafseer!!
        }
        return tafseerMutex.withLock {
            if (cachedTafseerFileName == fileName && cachedTafseer != null) {
                return@withLock cachedTafseer!!
            }
            try {
                val jsonString = context.assets.open(fileName)
                    .bufferedReader()
                    .use { it.readText() }

                json.decodeFromString<List<TafseerDto>>(jsonString).also {
                    cachedTafseerFileName = fileName
                    cachedTafseer = it
                }
            } catch (e: Exception) {
                emptyList()
            }
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
        val allSurahs = getAllSurahsFromAsset()
        val surah = surahsMap?.get(surahNumber) ?: allSurahs.find { it.id == surahNumber }
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
        val allSurahs = getAllSurahsFromAsset()
        val surah = surahsMap?.get(surahNumber) ?: allSurahs.find { it.id == surahNumber }
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
