package dev.sayed.mehrabalmomen.data.quran.mappers

import dev.sayed.mehrabalmomen.data.quran.local.dto.AyahDto
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