package com.autocoin.exchangegateway.dto.ticker

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.api.exchange.ticker.Ticker
import com.autocoin.exchangegateway.spi.exchange.ExchangeProvider
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker as SpiTicker

data class TickerDto(
    val exchange: String,
    val currencyPair: String,
    val ask: String,
    val bid: String,
    val baseCurrency24hVolume: String,
    val counterCurrency24hVolume: String,
    val receivedAtMillis: Long,
    val exchangeTimestampMillis: Long?,
) {
    fun toTicker(exchangeProvider: ExchangeProvider): SpiTicker = Ticker(
        exchange = exchangeProvider.getExchange(exchange),
        currencyPair = CurrencyPair.of(currencyPair),
        ask = ask.toBigDecimal(),
        bid = bid.toBigDecimal(),
        baseCurrency24hVolume = baseCurrency24hVolume.toBigDecimal(),
        counterCurrency24hVolume = counterCurrency24hVolume.toBigDecimal(),
        receivedAtMillis = receivedAtMillis,
        exchangeTimestampMillis = exchangeTimestampMillis,
    )

}

fun SpiTicker.toDto() = TickerDto(
    exchange = exchange.exchangeName,
    currencyPair = currencyPair.toString(),
    ask = ask.toPlainString(),
    bid = bid.toPlainString(),
    baseCurrency24hVolume = baseCurrency24hVolume.toPlainString(),
    counterCurrency24hVolume = counterCurrency24hVolume.toPlainString(),
    receivedAtMillis = receivedAtMillis,
    exchangeTimestampMillis = exchangeTimestampMillis,
)



