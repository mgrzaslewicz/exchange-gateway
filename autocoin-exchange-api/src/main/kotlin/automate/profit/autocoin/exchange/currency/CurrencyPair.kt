package automate.profit.autocoin.exchange.currency

import automate.profit.autocoin.exchange.util.CurrencyCache

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

        operator fun invoke(base: String, counter: String): CurrencyPair {
            return CurrencyPair(CurrencyCache.get(base), CurrencyCache.get(counter))
        }

        fun of(currencyPair: String): CurrencyPair {
            val split = currencyPair.indexOf('/')
            if (split < 1) {
                throw IllegalArgumentException("Could not parse currency pair from '$currencyPair'")
            }
            val base = currencyPair.substring(0, split)
            val counter = currencyPair.substring(split + 1)
            return CurrencyPair(base, counter).toUpperCase()
        }
    }

    fun contains(currency: String): Boolean = base == currency || counter == currency

    fun toUpperCase() = copy(
            base = base.toUpperCase(),
            counter = counter.toUpperCase()
    )

    override fun toString(): String = "${base.toUpperCase()}/${counter.toUpperCase()}"

    override fun compareTo(other: CurrencyPair): Int {
        return toString().compareTo(other.toString())
    }
}

