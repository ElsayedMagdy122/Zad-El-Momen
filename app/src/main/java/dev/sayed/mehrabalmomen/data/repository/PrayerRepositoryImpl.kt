package dev.sayed.mehrabalmomen.data.repository


import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.data.DateComponents
import dev.sayed.mehrabalmomen.data.toDomain
import dev.sayed.mehrabalmomen.data.toDomainName
import dev.sayed.mehrabalmomen.data.toPrayerTime
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
        val prayerTimes = getPrayerTimes(location = location, date = date)
        return prayerTimes.toDomain()
    }

    override fun getNextPrayer(
        instant: Instant,
        madhab: Madhab,
        calculationMethod: dev.sayed.mehrabalmomen.domain.entity.CalculationMethod,
        location: Location,
        date: LocalDate
    ): Prayer {
        val prayerTimes = getPrayerTimes(location = location, date = date)
        val nextAdhanPrayer = prayerTimes.nextPrayer(instant)
        val domainPrayerName = nextAdhanPrayer.toDomainName()
        return Prayer(
            name = domainPrayerName,
            time = prayerTimes.toPrayerTime(domainPrayerName)
        )
    }

    private fun getPrayerTimes(
        location: Location,
        date: LocalDate
    ): PrayerTimes {
        val coordinates = Coordinates(location.latitude, location.longitude)
        val date = DateComponents(date.year, date.month.number, date.day)
        val params = CalculationMethod.EGYPTIAN.parameters
        val prayerTimes = PrayerTimes(coordinates, date, params)
        return prayerTimes
    }
}