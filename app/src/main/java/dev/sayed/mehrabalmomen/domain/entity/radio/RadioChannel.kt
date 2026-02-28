package dev.sayed.mehrabalmomen.domain.entity.radio

import kotlin.time.Instant

data class RadioChannel(
    val id: Int,
    val nameAr: String,
    val nameEn: String,
    val streamUrl: String,
    val createdAt: Instant
)