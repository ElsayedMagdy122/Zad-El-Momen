package dev.sayed.mehrabalmomen.domain.repository

import dev.sayed.mehrabalmomen.domain.entity.time.CurrentTimeContext
import kotlinx.datetime.TimeZone
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/** Provides the current time and timezone through an injectable application boundary. */
@OptIn(ExperimentalTime::class)
interface TimeRepository {
    /**
     * Reads the current instant and timezone as one snapshot.
     *
     * Callers that derive local dates or countdown targets should prefer this method so all
     * calculations in one request use the same captured context.
     *
     * @return the instant and timezone captured for a single calculation cycle.
     */
    fun currentTimeContext(): CurrentTimeContext

    /**
     * Reads the current absolute instant.
     *
     * @return current point on the UTC timeline. Callers should read it once per calculation to
     * keep date derivation and countdown calculations internally consistent.
     */
    fun currentInstant(): Instant = currentTimeContext().instant

    /**
     * Reads the timezone currently used by the device and application.
     *
     * @return timezone used to convert the current instant into a local calendar date.
     */
    fun currentTimeZone(): TimeZone = currentTimeContext().timeZone
}
