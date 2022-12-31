package automate.profit.autocoin.exchange.util

import java.util.concurrent.ConcurrentHashMap

/**
 * This can significantly improve memory usage.
 * Under massive usage we had situations where over 50% of heap memory was used by duplicate currency and exchange names
 * This method does what normally String.intern() should do but with better control and much better effciency.
 * Details: https://shipilev.net/jvm/anatomy-quarks/10-string-intern/
 */
open class StringCache {

    private var cache: MutableMap<String, String> = ConcurrentHashMap()

    fun get(exchangeName: String): String {
        return cache.putIfAbsent(exchangeName, exchangeName) ?: exchangeName
    }

}

object ExchangeCache: StringCache()

object CurrencyCache: StringCache()