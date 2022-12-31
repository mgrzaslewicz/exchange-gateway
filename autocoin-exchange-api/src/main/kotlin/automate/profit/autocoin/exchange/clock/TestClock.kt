package automate.profit.autocoin.exchange.clock

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.util.*


/**
 * Cannot be moved to maven submodule due to cyclic dependency automate.profit:autocoin-exchange-api --> automate.profit:autocoin-exchange-api-test --> automate.profit:autocoin-exchange-api
 */
class QueueClock(timeMillisList: List<Long>) : Clock() {
    companion object {
        fun of(vararg timeMillis: Long): QueueClock {
            return QueueClock(timeMillis.toList())
        }

        fun of(vararg timeMillis: Int): QueueClock {
            return QueueClock(timeMillis.map { it.toLong() })
        }
    }

    private val timeMillisQueue: ArrayDeque<Long> = ArrayDeque(timeMillisList)

    override fun getZone(): ZoneId = ZoneId.systemDefault()

    override fun withZone(zone: ZoneId?): Clock {
        TODO("Not yet implemented")
    }

    override fun instant(): Instant {
        return Instant.ofEpochMilli(timeMillisQueue.poll())
    }

    fun noUnnecessaryTimeWasProvided() = timeMillisQueue.isEmpty()

}

class MutableFixedClock(var timeMillis: Long) : Clock() {

    companion object {
        fun of(instant: Instant): MutableFixedClock {
            return MutableFixedClock(instant.toEpochMilli())
        }
    }

    override fun getZone(): ZoneId = ZoneId.systemDefault()

    override fun withZone(zone: ZoneId?): Clock {
        TODO("Not yet implemented")
    }

    override fun instant(): Instant {
        return Instant.ofEpochMilli(timeMillis)
    }

}

