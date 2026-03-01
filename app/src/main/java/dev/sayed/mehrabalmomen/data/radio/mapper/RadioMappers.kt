package dev.sayed.mehrabalmomen.data.radio.mapper

import dev.sayed.mehrabalmomen.data.radio.dto.RadioChannelDto
import dev.sayed.mehrabalmomen.domain.entity.radio.RadioChannel
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun RadioChannelDto.toDomain(): RadioChannel {
    return RadioChannel(
        id = this.id,
        nameAr = this.nameAr,
        nameEn = this.nameEn,
        streamUrl = this.streamUrl,
        createdAt = Instant.parse(this.createdAt)
    )
}

fun List<RadioChannelDto>.toDomainList(): List<RadioChannel> = map { it.toDomain() }