package com.autocoin.exchangegateway.dto.ticker

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.api.exchange.ticker.Ticker
import com.autocoin.exchangegateway.dto.SerializableToJson
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
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
) : SerializableToJson {
    fun toTicker(): SpiTicker = Ticker(
        exchangeName = ExchangeName(exchange),
        currencyPair = CurrencyPair.of(currencyPair),
        ask = ask.toBigDecimal(),
        bid = bid.toBigDecimal(),
        baseCurrency24hVolume = baseCurrency24hVolume.toBigDecimal(),
        counterCurrency24hVolume = counterCurrency24hVolume.toBigDecimal(),
        receivedAtMillis = receivedAtMillis,
        exchangeTimestampMillis = exchangeTimestampMillis,
    )

    override fun appendJson(builder: StringBuilder) = builder
        .append("""{"exchange":"""")
        .append(exchange)
        .append("""","currencyPair":"""")
        .append(currencyPair)
        .append("""","ask":"""")
        .append(ask)
        .append("""","bid":"""")
        .append(bid)
        .append("""","baseCurrency24hVolume":"""")
        .append(baseCurrency24hVolume)
        .append("""","counterCurrency24hVolume":"""")
        .append(counterCurrency24hVolume)
        .append("""","receivedAtMillis":""")
        .append(receivedAtMillis)
        .append(""","exchangeTimestampMillis":""")
        .append(exchangeTimestampMillis)
        .append("""}""")
}

fun SpiTicker.toDto() = TickerDto(
    exchange = exchangeName.value,
    currencyPair = currencyPair.toString(),
    ask = ask.toPlainString(),
    bid = bid.toPlainString(),
    baseCurrency24hVolume = baseCurrency24hVolume.toPlainString(),
    counterCurrency24hVolume = counterCurrency24hVolume.toPlainString(),
    receivedAtMillis = receivedAtMillis,
    exchangeTimestampMillis = exchangeTimestampMillis,
)



