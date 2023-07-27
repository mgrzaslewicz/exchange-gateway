package com.autocoin.exchangegateway.api.exchange.ticker

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import java.math.BigDecimal
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker as SpiTicker

data class Ticker(
    override val exchange: Exchange,
    override val currencyPair: CurrencyPair,
    override val ask: BigDecimal,
    override val bid: BigDecimal,
    override val baseCurrency24hVolume: BigDecimal,
    override val counterCurrency24hVolume: BigDecimal,
    override val receivedAtMillis: Long,
    override val exchangeTimestampMillis: Long?,
) : SpiTicker
