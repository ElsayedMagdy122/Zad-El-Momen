package dev.sayed.mehrabalmomen.domain.entity.radio

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
data class RadioChannel(
    val id: Int,
    val nameAr: String,
    val nameEn: String,
    val streamUrl: String,
    val createdAt: Instant
)