package automate.profit.autocoin.exchange.currency

data class CurrencyPair(
        val base: String, // "base/counter" market
        val counter: String
) {
    companion object {
        fun of(currencyPair: String): CurrencyPair {
            val split = currencyPair.indexOf('/')
            if (split < 1) {
                throw IllegalArgumentException("Could not parse currency pair from '$currencyPair'")
            }
            val base = currencyPair.substring(0, split)
            val counter = currencyPair.substring(split + 1)
            return CurrencyPair(base, counter)
        }
    }

    override fun toString(): String {
        return "$base/$counter"
    }

}

fun CurrencyPair.toXchangeCurrencyPair() = org.knowm.xchange.currency.CurrencyPair(base, counter)
