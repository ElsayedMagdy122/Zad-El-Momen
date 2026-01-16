package dev.sayed.mehrabalmomen.data.local.quran.mappers

import dev.sayed.mehrabalmomen.data.local.quran.dto.AyahDto
import dev.sayed.mehrabalmomen.domain.entity.Ayah

fun AyahDto.toDomain(): Ayah {
    return Ayah(
        id = this.id,
        ayahNumber = this.ayahNumber,
        juzNumber = this.juzNumber,
        surahNumber = this.surahNumber,
        text = this.text
    )
}