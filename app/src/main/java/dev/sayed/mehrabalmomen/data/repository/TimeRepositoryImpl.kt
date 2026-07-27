package dev.sayed.mehrabalmomen.data.repository

import dev.sayed.mehrabalmomen.domain.repository.TimeRepository
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Production [TimeRepository] backed by the system clock and current device timezone. */
@OptIn(ExperimentalTime::class)
class TimeRepositoryImpl : TimeRepository {
    /**
     * Reads the system clock at the moment this function is called.
     *
     * @return current absolute [Instant] from [Clock.System].
     */
    override fun currentInstant(): Instant = Clock.System.now()

    /**
     * Reads the device's current system timezone at the moment this function is called.
     *
     * @return current [TimeZone], including any timezone change made since the previous call.
     */
    override fun currentTimeZone(): TimeZone = TimeZone.currentSystemDefault()
}
