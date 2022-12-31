package com.autocoin.exchangegateway.spi.exchange.ticker.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker

class InvalidCurrencyPairException(currencyPair: CurrencyPair) : Exception(currencyPair.toString())

interface TickerServiceGateway<T> {
    fun getTicker(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): Ticker

    fun getTickers(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPairs: Collection<CurrencyPair>,
    ): List<Ticker>
}


