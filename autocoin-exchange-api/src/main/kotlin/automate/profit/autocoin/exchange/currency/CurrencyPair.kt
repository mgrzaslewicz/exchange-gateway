package automate.profit.autocoin.exchange.currency

import automate.profit.autocoin.exchange.cache.CurrencyPairCache

/**
 * Private constructor + invoke operator override in order to apply memory optimization.
 * Note: Private constructor will be called if you call .copy().
 * This will bypass the optimization and thus should be avoided but should not influence code logic in any way.
 * https://stackoverflow.com/a/49561916
 */
data class CurrencyPair private constructor(
        val base: String, // "base/counter" market
        val counter: String
) : Comparable<CurrencyPair> {

    companion object {

        fun of(base: String, counter: String): CurrencyPair {
            val baseUpper = base.toUpperCase()
            val counterUpper = counter.toUpperCase()
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

    fun contains(currency: String): Boolean = base == currency || counter == currency

    fun toUpperCase() = copy(
            base = base.toUpperCase(),
            counter = counter.toUpperCase()
    )

    override fun toString(): String = "$base/$counter"

    fun toStringWithSeparator(separator: Char): String = "$base$separator$counter"

    override fun compareTo(other: CurrencyPair): Int {
        return toString().compareTo(other.toString())
    }
}

