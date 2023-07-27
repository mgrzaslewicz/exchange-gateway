package com.autocoin.exchangegateway.spi.exchange.ticker

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import java.math.BigDecimal

interface Ticker {
    val exchange: Exchange
    val currencyPair: CurrencyPair
    val ask: BigDecimal
    val bid: BigDecimal
    val baseCurrency24hVolume: BigDecimal
    val counterCurrency24hVolume: BigDecimal
    val receivedAtMillis: Long
    val exchangeTimestampMillis: Long?
    fun hasTimestamp() = this.exchangeTimestampMillis != null
}
