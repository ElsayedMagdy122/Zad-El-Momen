package dev.sayed.mehrabalmomen.data.prayer.repository


import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.data.DateComponents
import dev.sayed.mehrabalmomen.data.prayer.mapper.toAdhanMadhab
import dev.sayed.mehrabalmomen.data.prayer.mapper.toAdhanParams
import dev.sayed.mehrabalmomen.data.prayer.mapper.toDomainName
import dev.sayed.mehrabalmomen.data.prayer.mapper.toPrayerList
import dev.sayed.mehrabalmomen.data.prayer.mapper.toPrayerTime
import dev.sayed.mehrabalmomen.domain.entity.location.Location
import dev.sayed.mehrabalmomen.domain.entity.prayer.CalculationMethod
import dev.sayed.mehrabalmomen.domain.entity.prayer.Madhab
import dev.sayed.mehrabalmomen.domain.entity.prayer.Prayer
import dev.sayed.mehrabalmomen.domain.repository.prayer.PrayerRepository
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class PrayerRepositoryImpl : PrayerRepository {


    override  fun getDailyPrayers(
        madhab: Madhab,
        calculationMethod: CalculationMethod,
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
        calculationMethod: CalculationMethod,
        location: Location,
        date: LocalDate
    ): Prayer {
        val prayerTimes = getPrayerTimes(location, date, madhab, calculationMethod)

    val prayersToday = listOf(
        Prayer.PrayerName.FAJR to prayerTimes.fajr,
        Prayer.PrayerName.ZUHR to prayerTimes.dhuhr,
        Prayer.PrayerName.ASR to prayerTimes.asr,
        Prayer.PrayerName.MAGHRIB to prayerTimes.maghrib,
        Prayer.PrayerName.ISHA to prayerTimes.isha
    )


    val next = prayersToday.firstOrNull { it.second > instant }

    return if (next != null) {
        Prayer(name = next.first, time = next.second)
    } else {
        val tomorrow = date.plus(1, DateTimeUnit.DAY)
        val prayerTimesTomorrow = getPrayerTimes(location, tomorrow, madhab, calculationMethod)
        Prayer(name = Prayer.PrayerName.FAJR, time = prayerTimesTomorrow.fajr)
    }}
    private fun getPrayerTimes(
        location: Location,
        date: LocalDate,
        madhab: Madhab,
        calculationMethod: CalculationMethod
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