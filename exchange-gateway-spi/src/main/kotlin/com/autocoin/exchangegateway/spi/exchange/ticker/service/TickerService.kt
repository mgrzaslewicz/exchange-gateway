package com.autocoin.exchangegateway.spi.exchange.ticker.service

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker

interface TickerService<T> {
    val exchange: Exchange
    fun getTicker(
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): Ticker

    fun getTickers(
        apiKey: ApiKeySupplier<T>,
        currencyPairs: Collection<CurrencyPair>,
    ): List<Ticker>
}

