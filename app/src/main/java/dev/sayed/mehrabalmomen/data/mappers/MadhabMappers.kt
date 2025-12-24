package dev.sayed.mehrabalmomen.data.mappers

import dev.sayed.mehrabalmomen.domain.entity.Madhab

fun Madhab.toAdhanMadhab(): com.batoulapps.adhan2.Madhab {
    return when (this) {
        Madhab.HANAFI -> com.batoulapps.adhan2.Madhab.HANAFI
        Madhab.SHAFI -> com.batoulapps.adhan2.Madhab.SHAFI
    }
}