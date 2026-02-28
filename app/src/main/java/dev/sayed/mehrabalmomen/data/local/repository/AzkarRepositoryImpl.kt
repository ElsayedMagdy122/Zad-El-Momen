package dev.sayed.mehrabalmomen.data.local.repository

import dev.sayed.mehrabalmomen.data.local.AzkarLocalDataSource
import dev.sayed.mehrabalmomen.data.local.dto.AzkarCategoryDto
import dev.sayed.mehrabalmomen.data.mappers.toDomain
import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarCategory
import dev.sayed.mehrabalmomen.domain.repository.AzkarRepository

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