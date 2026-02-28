@file:OptIn(ExperimentalTime::class)

package dev.sayed.mehrabalmomen.domain.entity

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class RadioChannel(
    val id: Int,
    val nameAr: String,
    val nameEn: String,
    val streamUrl: String,
    val createdAt: Instant
)