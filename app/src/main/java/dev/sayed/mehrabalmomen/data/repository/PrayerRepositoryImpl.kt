package dev.sayed.mehrabalmomen.data.repository


import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.data.DateComponents
import dev.sayed.mehrabalmomen.data.mappers.toAdhanMadhab
import dev.sayed.mehrabalmomen.data.mappers.toAdhanParams
import dev.sayed.mehrabalmomen.data.mappers.toDomainName
import dev.sayed.mehrabalmomen.data.mappers.toPrayerList
import dev.sayed.mehrabalmomen.data.mappers.toPrayerTime
import dev.sayed.mehrabalmomen.domain.entity.Location
import dev.sayed.mehrabalmomen.domain.entity.Madhab
import dev.sayed.mehrabalmomen.domain.entity.Prayer
import dev.sayed.mehrabalmomen.domain.repository.PrayerRepository
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PrayerRepositoryImpl : PrayerRepository {


    override suspend fun getDailyPrayers(
        madhab: Madhab,
        calculationMethod: dev.sayed.mehrabalmomen.domain.entity.CalculationMethod,
        location: Location,
        date: LocalDate
    ): List<Prayer> {
        val prayerTimes = getPrayerTimes(
            location = location,
            date = date,
            madhab = madhab,
            calculationMethod = calculationMethod
        )
        return prayerTimes.toPrayerList()
    }

    override fun getNextPrayer(
        instant: Instant,
        madhab: Madhab,
        calculationMethod: dev.sayed.mehrabalmomen.domain.entity.CalculationMethod,
        location: Location,
        date: LocalDate
    ): Prayer {
        val prayerTimes = getPrayerTimes(
            location = location,
            date = date,
            madhab = madhab,
            calculationMethod = calculationMethod
        )
        val nextAdhanPrayer = prayerTimes.nextPrayer(instant)
        val domainPrayerName = nextAdhanPrayer.toDomainName()
        return Prayer(
            name = domainPrayerName,
            time = prayerTimes.toPrayerTime(domainPrayerName)
        )
    }

    private fun getPrayerTimes(
        location: Location,
        date: LocalDate,
        madhab: Madhab,
        calculationMethod: dev.sayed.mehrabalmomen.domain.entity.CalculationMethod
    ): PrayerTimes {

        val coordinates = Coordinates(location.latitude, location.longitude)

        val dateComponents = DateComponents(
            date.year,
            date.month.number,
            date.day
        )

        val baseParams = calculationMethod.toAdhanParams()
        val params = baseParams.copy(madhab = madhab.toAdhanMadhab())
        return PrayerTimes(
            coordinates = coordinates,
            dateComponents = dateComponents,
            calculationParameters = params
        )
    }
}