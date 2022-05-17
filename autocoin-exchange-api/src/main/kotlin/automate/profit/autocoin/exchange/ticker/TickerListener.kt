package automate.profit.autocoin.exchange.ticker

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import java.math.BigDecimal
import java.time.Instant

data class Ticker(
        val currencyPair: CurrencyPair,
        val ask: BigDecimal,
        val bid: BigDecimal,
        val baseCurrency24hVolume: BigDecimal,
        val counterCurrency24hVolume: BigDecimal,
        val receivedAtMillis: Long,
        val exchangeTimestampMillis: Long?,
) {
    fun hasTimestamp() = this.exchangeTimestampMillis != null
}

interface TickerListener {

    fun onTicker(exchange: SupportedExchange, currencyPair: CurrencyPair, ticker: Ticker)

    /**
     * There was no new ticker on exchange but time has passed
     * @param ticker might be the same that was already fetched from exchange or none
     */
    fun onNoNewTicker(exchange: SupportedExchange, currencyPair: CurrencyPair, ticker: Ticker?) {}

}
