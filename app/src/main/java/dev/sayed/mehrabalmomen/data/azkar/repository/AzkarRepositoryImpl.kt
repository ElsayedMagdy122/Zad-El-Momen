package dev.sayed.mehrabalmomen.data.azkar.repository

import dev.sayed.mehrabalmomen.data.azkar.local.AzkarLocalDataSource
import dev.sayed.mehrabalmomen.data.azkar.local.dto.AzkarCategoryDto
import dev.sayed.mehrabalmomen.data.azkar.mapper.toDomain
import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarCategory
import dev.sayed.mehrabalmomen.domain.repository.azkar.AzkarRepository

class AzkarRepositoryImpl(
    private val localDataSource: AzkarLocalDataSource
) : AzkarRepository {

    override suspend fun getAzkarCategories(): List<AzkarCategory> {
        val rawData =  localDataSource.getAzkar()
        val categories = rawData.map { (title, items) ->
            AzkarCategoryDto(title = title, items = items)
        }
        return categories.map { it.toDomain() }
    }
}