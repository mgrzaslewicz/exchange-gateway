package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.currency.CurrencyPair
import java.math.BigDecimal
import java.time.Instant

data class Ticker(
        val currencyPair: CurrencyPair,
        val last: BigDecimal,
        val ask: BigDecimal,
        val bid: BigDecimal,
        val timestamp: Instant?
) {
    fun hasTimestamp() = this.timestamp != null
}

interface TickerListener {

    fun onTicker(ticker: Ticker)

    /**
     * There was no new ticker on exchange but time has passed
     */
    fun onNoNewTicker() {}

    fun currencyPair(): CurrencyPair

}
