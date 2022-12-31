package automate.profit.autocoin.exchange.cache

import automate.profit.autocoin.exchange.currency.CurrencyPair

object CurrencyPairCache : Cache<String, CurrencyPair>() {
    fun get(base: String, counter: String, valueFunction: () -> CurrencyPair): CurrencyPair {
        val currencyPairString = CurrencyStringCache.get(base + counter)
        return super.get(currencyPairString, valueFunction)
    }
}