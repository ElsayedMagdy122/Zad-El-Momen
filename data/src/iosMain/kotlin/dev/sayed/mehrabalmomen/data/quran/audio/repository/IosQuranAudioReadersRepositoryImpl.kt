package dev.sayed.mehrabalmomen.data.quran.audio.repository

import dev.sayed.mehrabalmomen.data.quran.audio.local.dao.DownloadedReciterDao
import dev.sayed.mehrabalmomen.data.quran.audio.local.entity.DownloadedReciterEntity
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioReadersRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.toEntity
import dev.sayed.mehrabalmomen.data.settings.local.RecitationPreferences
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioReader
import dev.sayed.mehrabalmomen.domain.model.audio.DownloadStatus
import dev.sayed.mehrabalmomen.domain.model.audio.ReciterPreference
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioReadersRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

class IosQuranAudioReadersRepositoryImpl(
    private val readersRemoteDataSource: QuranAudioReadersRemoteDataSource,
    private val downloadedReciterDao: DownloadedReciterDao,
    private val recitationPreferences: RecitationPreferences,
    private val httpClient: HttpClient
) : QuranAudioReadersRepository {

    private val scope = CoroutineScope(Default)
    private val downloadStatuses = mutableMapOf<String, MutableStateFlow<DownloadStatus>>()

    override suspend fun getReaders(): List<QuranAudioReader> {
        val readersDto = readersRemoteDataSource.getReaders()
        val rewayatDto = readersRemoteDataSource.getRewayat()

        val rewayatMap = rewayatDto
            .map { it.toEntity() }
            .associateBy { it.id }

        return readersDto.map { readerDto ->
            val rewayaEntity = readerDto.rewayaId?.let { rewayatMap[it] }
            readerDto.toEntity(rewaya = rewayaEntity)
        }
    }

    override fun getDownloadedReciters(surahId: Int): Flow<List<QuranAudioReader>> {
        return downloadedReciterDao.getDownloadedRecitersForSurah(surahId).map { entities ->
            entities.map { it.toEntity() }
        }
    }

    override suspend fun getDownloadedRecitersOnce(): List<QuranAudioReader> {
        return downloadedReciterDao.getAllDownloadedRecitersOnce().map { it.toEntity() }
    }

    override suspend fun isReciterDownloaded(reciterId: Int, surahId: Int): Boolean {
        return downloadedReciterDao.getDownloadedReciter(reciterId, surahId) != null
    }

    override suspend fun getDownloadedReciter(reciterId: Int, surahId: Int): QuranAudioReader? {
        return downloadedReciterDao.getDownloadedReciter(reciterId, surahId)?.toEntity()
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun downloadReciter(reciter: QuranAudioReader, surahId: Int) {
        val key = "${reciter.id}_$surahId"
        val statusFlow = downloadStatuses.getOrPut(key) { MutableStateFlow(DownloadStatus(0, DownloadStatus.State.DOWNLOADING)) }
        statusFlow.value = DownloadStatus(0, DownloadStatus.State.DOWNLOADING)

        scope.launch {
            try {
                val formattedSurahId = surahId.toString().padStart(3, '0')
                val cleanBaseUrl = reciter.baseAudioUrl.trimEnd('/')
                val fullAudioUrl = "$cleanBaseUrl/$formattedSurahId.mp3"

                val bytes = httpClient.get(fullAudioUrl).readBytes()

                val documentsDirectory = NSFileManager.defaultManager.URLForDirectory(
                    directory = NSDocumentDirectory,
                    inDomain = NSUserDomainMask,
                    appropriateForURL = null,
                    create = true,
                    error = null
                )
                val fileName = "reciter_${reciter.id}_surah_$surahId.mp3"
                val fileUrl = documentsDirectory?.URLByAppendingPathComponent(fileName)
                val filePath = fileUrl?.path

                downloadedReciterDao.insertDownloadedReciter(
                    DownloadedReciterEntity(
                        id = reciter.id,
                        surahId = surahId,
                        nameAr = reciter.nameAr,
                        nameEn = reciter.nameEn,
                        rewayaName = reciter.rewaya?.nameAr ?: "",
                        baseAudioUrl = reciter.baseAudioUrl,
                        localFilePath = filePath ?: ""
                    )
                )
                statusFlow.value = DownloadStatus(100, DownloadStatus.State.COMPLETED)
            } catch (ex: Exception) {
                statusFlow.value = DownloadStatus(0, DownloadStatus.State.FAILED)
            }
        }
    }

    override fun observeDownloadStatus(reciterId: Int, surahId: Int): Flow<DownloadStatus> {
        val key = "${reciterId}_$surahId"
        return downloadStatuses.getOrPut(key) { MutableStateFlow(DownloadStatus(0, DownloadStatus.State.IDLE)) }
    }

    override suspend fun saveLastReciter(preference: ReciterPreference) {
        recitationPreferences.saveLastReciter(
            id = preference.id,
            nameAr = preference.nameAr,
            nameEn = preference.nameEn,
            baseAudioUrl = preference.baseAudioUrl,
            rewayaName = preference.rewayaName
        )
    }

    override fun observeLastReciter(): Flow<ReciterPreference?> {
        return recitationPreferences.lastReciter.map { pref ->
            pref?.let {
                ReciterPreference(
                    id = it.id,
                    nameAr = it.nameAr,
                    nameEn = it.nameEn,
                    baseAudioUrl = it.baseAudioUrl,
                    rewayaName = it.rewayaName
                )
            }
        }
    }
}
