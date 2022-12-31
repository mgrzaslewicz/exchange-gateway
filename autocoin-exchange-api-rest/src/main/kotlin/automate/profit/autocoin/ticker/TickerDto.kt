package automate.profit.autocoin.ticker

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.ticker.Ticker
import java.time.Instant

data class TickerDto(
        val exchange: String,
        val currencyPair: String,
        val ask: Double,
        val bid: Double,
        val baseCurrency24hVolume: Double,
        val counterCurrency24hVolume: Double,
        val timestampMillis: Long?
) {
    fun toTicker() = Ticker(
            currencyPair = CurrencyPair.Companion.of(currencyPair),
            ask = ask.toBigDecimal(),
            bid = bid.toBigDecimal(),
            baseCurrency24hVolume = baseCurrency24hVolume.toBigDecimal(),
            counterCurrency24hVolume = counterCurrency24hVolume.toBigDecimal(),
            timestamp = if (timestampMillis != null) Instant.ofEpochMilli(timestampMillis) else null
    )
}

fun Ticker.toDto(exchange: SupportedExchange) = TickerDto(
        exchange = exchange.exchangeName,
        currencyPair = currencyPair.toString(),
        ask = ask.toDouble(),
        bid = bid.toDouble(),
        baseCurrency24hVolume = baseCurrency24hVolume.toDouble(),
        counterCurrency24hVolume = counterCurrency24hVolume.toDouble(),
        timestampMillis = timestamp?.toEpochMilli()
)
