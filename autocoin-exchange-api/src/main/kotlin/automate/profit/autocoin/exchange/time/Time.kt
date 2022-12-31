package automate.profit.autocoin.exchange.time

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*


interface TimeMillisProvider {
    fun now() = System.currentTimeMillis()
}

class SystemTimeMillisProvider : TimeMillisProvider

/**
 * Cannot be moved to maven submodule due to cyclic dependency automate.profit:autocoin-exchange-api --> automate.profit:autocoin-exchange-api-test --> automate.profit:autocoin-exchange-api
 */
class QueueClock(timeMillisList: List<Long>) : Clock() {
    private val timeMillisQueue: ArrayDeque<Long> = ArrayDeque(timeMillisList)

    override fun getZone(): ZoneId = ZoneId.systemDefault()

    override fun withZone(zone: ZoneId?): Clock {
        TODO("Not yet implemented")
    }

    override fun instant(): Instant {
        return Instant.ofEpochMilli(timeMillisQueue.poll())
    }

    fun assertNoUnnecessaryTimeWasProvided() {
        assert(timeMillisQueue.isEmpty())
    }

}

