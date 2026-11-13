package dev.sayed.mehrabalmomen.data.azkar.local.dto

import kotlinx.serialization.Serializable

@Serializable
data class AzkarCategoryDto(
    val title: String,
    val items: List<AzkarItemDto>
)