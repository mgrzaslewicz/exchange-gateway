package automate.profit.autocoin.spi.exchange.currency

interface CurrencyPair : Comparable<CurrencyPair> {
    val base: String // base/counter market
    val counter: String
    fun contains(currency: String): Boolean = base == currency || counter == currency
    fun toStringWithSeparator(separator: Char = '/'): String = "$base$separator$counter"
    override fun compareTo(other: CurrencyPair) = toString().compareTo(other.toString())
    fun toUpperCase(): CurrencyPair
}
