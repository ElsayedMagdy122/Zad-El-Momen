package dev.sayed.mehrabalmomen.data.mappers

import dev.sayed.mehrabalmomen.data.local.dto.AzkarCategoryDto
import dev.sayed.mehrabalmomen.data.local.dto.AzkarItemDto
import dev.sayed.mehrabalmomen.domain.entity.AzkarCategory
import dev.sayed.mehrabalmomen.domain.entity.AzkarItem

fun AzkarItemDto.toDomain() = AzkarItem(
    content = content,
    count = count,
    description = description.orEmpty(),
    reference = reference.orEmpty()
)

fun AzkarCategoryDto.toDomain() = AzkarCategory(
    title = title,
    items = items.map { it.toDomain() }
)