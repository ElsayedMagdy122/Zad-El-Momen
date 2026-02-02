package dev.sayed.mehrabalmomen.data.local.dto

import kotlinx.serialization.Serializable

@Serializable
data class AzkarCategoryDto(
    val title: String,
    val items: List<AzkarItemDto>
)