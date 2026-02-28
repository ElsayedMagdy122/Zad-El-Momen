package dev.sayed.mehrabalmomen.domain.entity.azkar

import dev.sayed.mehrabalmomen.domain.entity.azkar.AzkarItem

data class AzkarCategory(
    val title: String,
    val items: List<AzkarItem>
)