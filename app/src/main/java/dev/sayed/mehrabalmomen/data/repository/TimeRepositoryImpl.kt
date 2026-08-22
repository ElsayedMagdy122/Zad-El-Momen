package dev.sayed.mehrabalmomen.data.repository

import dev.sayed.mehrabalmomen.domain.entity.time.CurrentTimeContext
import dev.sayed.mehrabalmomen.domain.repository.TimeRepository
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/** Production [TimeRepository] backed by the system clock and current device timezone. */
@OptIn(ExperimentalTime::class)
class TimeRepositoryImpl : TimeRepository {
    /**
     * Captures the current system time and device timezone together for one calculation cycle.
     *
     * @return an immutable time context containing the instant and timezone observed by this call.
     */
    override fun currentTimeContext(): CurrentTimeContext = CurrentTimeContext(
        instant = Clock.System.now(),
        timeZone = TimeZone.currentSystemDefault(),
    )

    /**
     * Reads the system clock at the moment this function is called.
     *
     * @return current absolute instant from [Clock.System].
     */
    override fun currentInstant() = Clock.System.now()

    /**
     * Reads the device's current system timezone at the moment this function is called.
     *
     * @return current [TimeZone], including any timezone change made since the previous call.
     */
    override fun currentTimeZone(): TimeZone = TimeZone.currentSystemDefault()
}
