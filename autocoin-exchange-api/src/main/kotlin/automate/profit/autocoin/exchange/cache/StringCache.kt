package automate.profit.autocoin.exchange.cache

/**
 * This can significantly improve memory usage.
 * Under massive usage we had situations where over 50% of heap memory was used by duplicate currency and exchange names
 * This method does what normally String.intern() should do but with better control and much better efficiency.
 * Details: https://shipilev.net/jvm/anatomy-quarks/10-string-intern/
 */
open class StringCache : Cache<String, String>() {
    fun get(key: String) = super.get(key) { key }
}

object ExchangeCache : StringCache()

object CurrencyStringCache : StringCache()

object ExchangeWithCurrencyPairStringCache : StringCache()