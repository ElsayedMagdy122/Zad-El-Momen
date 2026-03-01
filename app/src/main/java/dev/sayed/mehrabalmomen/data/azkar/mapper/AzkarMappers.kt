package dev.sayed.mehrabalmomen.data.azkar.mapper

import dev.sayed.mehrabalmomen.data.azkar.local.dto.AzkarCategoryDto
import dev.sayed.mehrabalmomen.data.azkar.local.dto.AzkarItemDto
import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarCategory
import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarItem

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