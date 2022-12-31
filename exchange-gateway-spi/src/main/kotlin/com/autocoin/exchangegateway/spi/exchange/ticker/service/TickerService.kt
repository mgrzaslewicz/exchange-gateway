package com.autocoin.exchangegateway.spi.exchange.ticker.service

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker

interface TickerService<T> {
    val exchangeName: ExchangeName
    fun getTicker(
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): Ticker

    fun getTickers(
        apiKey: ApiKeySupplier<T>,
        currencyPairs: Collection<CurrencyPair>,
    ): List<Ticker>
}

