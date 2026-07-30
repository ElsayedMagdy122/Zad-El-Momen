package dev.sayed.mehrabalmomen.data.quran.audio.repository

import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioReadersRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.QuranAudioRemoteDataSource
import dev.sayed.mehrabalmomen.data.quran.audio.remote.toEntity
import dev.sayed.mehrabalmomen.domain.entity.quran.audio.QuranAudioReader
import dev.sayed.mehrabalmomen.domain.repository.quran.QuranAudioReadersRepository


class QuranAudioReadersRepositoryImpl(
    private val readersRemoteDataSource: QuranAudioReadersRemoteDataSource
) : QuranAudioReadersRepository {

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
}