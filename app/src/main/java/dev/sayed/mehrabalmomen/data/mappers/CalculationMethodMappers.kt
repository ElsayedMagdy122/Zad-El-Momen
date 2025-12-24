package dev.sayed.mehrabalmomen.data.mappers

import com.batoulapps.adhan2.CalculationMethod

fun dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.toAdhanParams() =
    when (this) {
        dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.EGYPTIAN -> {
            CalculationMethod.EGYPTIAN.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.MUSLIM_WORLD_LEAGUE -> {
            CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.KARACHI -> {
            CalculationMethod.KARACHI.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.UMM_AL_QURA -> {
            CalculationMethod.UMM_AL_QURA.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.DUBAI -> {
            CalculationMethod.DUBAI.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.QATAR -> {
            CalculationMethod.QATAR.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.KUWAIT -> {
            CalculationMethod.KUWAIT.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.MOONSIGHTING_COMMITTEE -> {
            CalculationMethod.MOON_SIGHTING_COMMITTEE.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.SINGAPORE -> {
            CalculationMethod.SINGAPORE.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.NORTH_AMERICA -> {
            CalculationMethod.NORTH_AMERICA.parameters
        }

        dev.sayed.mehrabalmomen.domain.entity.CalculationMethod.OTHER -> {
            CalculationMethod.OTHER.parameters
        }
    }