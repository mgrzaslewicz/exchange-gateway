package automate.profit.autocoin.exchange.time


interface TimeMillisProvider {
    fun now() = System.currentTimeMillis()
}

class SystemTimeMillisProvider : TimeMillisProvider

