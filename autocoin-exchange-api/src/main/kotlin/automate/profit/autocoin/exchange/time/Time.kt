package automate.profit.autocoin.exchange.time

import java.util.*


interface TimeMillisProvider {
    fun now() = System.currentTimeMillis()
}

class SystemTimeMillisProvider : TimeMillisProvider

/**
 * Cannot be moved to maven submodule due to cyclic dependency automate.profit:autocoin-exchange-api --> automate.profit:autocoin-exchange-api-test --> automate.profit:autocoin-exchange-api
 */
class TestQueueTimeMillisProvider(private val timeMillisList: List<Long>) : TimeMillisProvider {
    private val timeMillisQueue: ArrayDeque<Long> = ArrayDeque(timeMillisList)
    override fun now(): Long {
        return timeMillisQueue.poll()
    }

    fun assertNoUnnecessaryTimeWasProvided() {
        assert(timeMillisQueue.isEmpty())
    }
}

class TestFixedTimeMillisProvider(var currentTimeMs: Long) : TimeMillisProvider {
    override fun now(): Long {
        return currentTimeMs
    }
}
