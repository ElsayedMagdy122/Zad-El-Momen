package dev.sayed.mehrabalmomen.data.local.quran.mappers

import dev.sayed.mehrabalmomen.data.local.quran.dto.AyahDto
import dev.sayed.mehrabalmomen.domain.entity.quran.Ayah

fun AyahDto.toDomain(surahNumber: Int,surahNameArabic:String="",surahNameEnglish:String=""): Ayah {
    return Ayah(
        id = this.id,
        ayahNumber = this.ayahNumber,
        juzNumber = this.juzNumber,
        surahNumber = surahNumber,
        text = this.text,
        surahNameArabic = surahNameArabic,
        surahNameEnglish = surahNameEnglish
    )
}