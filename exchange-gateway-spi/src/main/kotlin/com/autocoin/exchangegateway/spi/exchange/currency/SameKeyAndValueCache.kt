package com.autocoin.exchangegateway.spi.exchange.currency

/**
 * This can significantly improve memory usage.
 * Under massive usage we had situations where over 50% of heap memory was used by duplicate currency and exchange names
 * This method does what normally String.intern() should do but with better control and much better efficiency.
 * Details: https://shipilev.net/jvm/anatomy-quarks/10-string-intern/
 */
open class SameKeyAndValueCache<T> : Cache<T, T>() {
    fun get(key: T) = super.get(key) { key }
}

object ExchangeCache : SameKeyAndValueCache<com.autocoin.exchangegateway.spi.exchange.ExchangeName>()

object CurrencyStringCache : SameKeyAndValueCache<String>()

object ExchangeWithCurrencyPairStringCache : SameKeyAndValueCache<String>()
