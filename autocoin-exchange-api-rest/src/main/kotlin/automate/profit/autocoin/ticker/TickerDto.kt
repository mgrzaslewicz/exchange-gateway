package automate.profit.autocoin.ticker

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.ticker.Ticker
import java.time.Instant

data class TickerDto(
        val currencyPair: CurrencyPair,
        val last: Double,
        val ask: Double,
        val bid: Double,
        val timestampMillis: Long?
) {
    fun toTicker() = Ticker(
            currencyPair = currencyPair,
            last = last.toBigDecimal(),
            ask = ask.toBigDecimal(),
            bid = bid.toBigDecimal(),
            timestamp = if (timestampMillis != null) Instant.ofEpochMilli(timestampMillis) else null
    )
}

fun Ticker.toDto() = TickerDto(
        currencyPair = this.currencyPair.toUpperCase(),
        last = this.last.toDouble(),
        ask = this.ask.toDouble(),
        bid = this.bid.toDouble(),
        timestampMillis = this.timestamp?.toEpochMilli()
)
