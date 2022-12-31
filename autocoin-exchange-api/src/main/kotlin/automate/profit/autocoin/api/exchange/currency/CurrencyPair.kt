package automate.profit.autocoin.api.exchange.currency

import automate.profit.autocoin.spi.exchange.currency.Cache
import automate.profit.autocoin.spi.exchange.currency.CurrencyStringCache
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair as SpiCurrencyPair

object CurrencyPairCache : Cache<String, CurrencyPair>() {
    fun get(base: String, counter: String, valueFunction: () -> CurrencyPair): CurrencyPair {
        val currencyPairString = CurrencyStringCache.get(base + counter)
        return super.get(currencyPairString, valueFunction)
    }
}

/**
 * Private constructor + invoke operator override in order to apply memory optimization.
 * Note: Private constructor will be called if you call .copy().
 * This will bypass the optimization and thus should be avoided but should not influence code logic in any way.
 * https://stackoverflow.com/a/49561916
 */
data class CurrencyPair private constructor(
    override val base: String, // "base/counter" market
    override val counter: String,
) : SpiCurrencyPair {

    companion object {

        fun of(base: String, counter: String): CurrencyPair {
            val baseUpper = base.uppercase()
            val counterUpper = counter.uppercase()
            return CurrencyPairCache.get(baseUpper, counterUpper) { CurrencyPair(baseUpper, counterUpper) }
        }

        fun of(currencyPair: String): CurrencyPair {
            val split = currencyPair.indexOf('/')
            if (split < 1) {
                throw IllegalArgumentException("Could not parse currency pair from '$currencyPair'")
            }
            val base = currencyPair.substring(0, split)
            val counter = currencyPair.substring(split + 1)
            return of(base, counter)
        }
    }

    override fun contains(currency: String): Boolean = base == currency || counter == currency

    override fun toUpperCase() = copy(
        base = base.uppercase(),
        counter = counter.uppercase(),
    )

    override fun toString(): String = "$base/$counter"

}

