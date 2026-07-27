package dev.sayed.mehrabalmomen.data.prayer.mapper

import com.batoulapps.adhan2.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod as domainCalculationMethod


/**
 * Maps an application calculation method to a complete Adhan2 parameter set.
 *
 * @receiver domain calculation method saved in prayer settings.
 * @return usable Adhan2 parameters for calculating all five obligatory prayers. The domain
 * `OTHER` value is not user-selectable and therefore follows the application's existing Muslim
 * World League fallback instead of Adhan2's unconfigured zero-angle `OTHER` parameters.
 */
fun domainCalculationMethod.toAdhanParams() =
    when (this) {
        domainCalculationMethod.EGYPTIAN -> {
            CalculationMethod.EGYPTIAN.parameters
        }

        domainCalculationMethod.MUSLIM_WORLD_LEAGUE -> {
            CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
        }

        domainCalculationMethod.KARACHI -> {
            CalculationMethod.KARACHI.parameters
        }

        domainCalculationMethod.UMM_AL_QURA -> {
            CalculationMethod.UMM_AL_QURA.parameters
        }

        domainCalculationMethod.DUBAI -> {
            CalculationMethod.DUBAI.parameters
        }

        domainCalculationMethod.QATAR -> {
            CalculationMethod.QATAR.parameters
        }

        domainCalculationMethod.KUWAIT -> {
            CalculationMethod.KUWAIT.parameters
        }

        domainCalculationMethod.MOONSIGHTING_COMMITTEE -> {
            CalculationMethod.MOON_SIGHTING_COMMITTEE.parameters
        }

        domainCalculationMethod.SINGAPORE -> {
            CalculationMethod.SINGAPORE.parameters
        }

        domainCalculationMethod.NORTH_AMERICA -> {
            CalculationMethod.NORTH_AMERICA.parameters
        }

        domainCalculationMethod.OTHER -> {
            CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
        }
    }