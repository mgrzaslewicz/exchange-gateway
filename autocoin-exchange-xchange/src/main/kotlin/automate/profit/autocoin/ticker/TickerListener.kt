package automate.profit.autocoin.ticker

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.currency.toXchangeCurrencyPair
import java.math.BigDecimal
import java.time.Instant
import java.util.*
import org.knowm.xchange.dto.marketdata.Ticker as XchangeTicker

data class Ticker(
        val currencyPair: CurrencyPair,
        val last: BigDecimal,
        val ask: BigDecimal,
        val bid: BigDecimal,
        val timestamp: Instant?
) {
    fun hasTimestamp() = this.timestamp != null
}

fun Ticker.toXchangeTicker(): XchangeTicker = XchangeTicker.Builder()
        .currencyPair(this.currencyPair.toXchangeCurrencyPair())
        .last(this.last)
        .ask(this.ask)
        .bid(this.bid)
        .timestamp(this.timestamp?.toEpochMilli()?.let { Date(it) })
        .build()

fun XchangeTicker.toTicker() = Ticker(
        currencyPair = CurrencyPair(base = this.currencyPair.base.currencyCode, counter = this.currencyPair.counter.currencyCode),
        last = this.last,
        ask = this.ask,
        bid = this.bid,
        timestamp = this.timestamp?.toInstant()
)

interface TickerListener {

    fun onTicker(ticker: Ticker)

    fun isActive(): Boolean = true

    fun currencyPair(): CurrencyPair

}
