package dev.sayed.mehrabalmomen.data.mappers

import com.batoulapps.adhan2.CalculationMethod

fun dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod.toAdhanParams() =
    when (this) {
        dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod.EGYPTIAN -> {
            CalculationMethod.EGYPTIAN.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod.MUSLIM_WORLD_LEAGUE -> {
            CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod.KARACHI -> {
            CalculationMethod.KARACHI.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod.UMM_AL_QURA -> {
            CalculationMethod.UMM_AL_QURA.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod.DUBAI -> {
            CalculationMethod.DUBAI.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod.QATAR -> {
            CalculationMethod.QATAR.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod.KUWAIT -> {
            CalculationMethod.KUWAIT.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod.MOONSIGHTING_COMMITTEE -> {
            CalculationMethod.MOON_SIGHTING_COMMITTEE.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod.SINGAPORE -> {
            CalculationMethod.SINGAPORE.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod.NORTH_AMERICA -> {
            CalculationMethod.NORTH_AMERICA.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod.OTHER -> {
            CalculationMethod.OTHER.parameters
        }
    }