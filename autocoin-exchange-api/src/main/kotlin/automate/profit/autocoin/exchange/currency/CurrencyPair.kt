package automate.profit.autocoin.exchange.currency

data class CurrencyPair(
        val base: String, // "base/counter" market
        val counter: String
) : Comparable<CurrencyPair> {
    companion object {
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

