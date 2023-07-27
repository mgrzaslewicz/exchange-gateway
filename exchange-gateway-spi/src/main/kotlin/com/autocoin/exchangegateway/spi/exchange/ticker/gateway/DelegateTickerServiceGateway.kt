package com.autocoin.exchangegateway.spi.exchange.ticker.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker
import com.autocoin.exchangegateway.spi.exchange.ticker.service.TickerService

class DelegateTickerServiceGateway<T>(
    private val tickerServiceGateways: Map<Exchange, TickerService<T>>,
) : TickerServiceGateway<T> {

    override fun getTicker(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): Ticker {
        return tickerServiceGateways.getValue(exchange).getTicker(
            apiKey = apiKey,
            currencyPair = currencyPair,
        )
    }

    override fun getTickers(
        exchangeName: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPairs: Collection<CurrencyPair>,
    ): List<Ticker> {
        return tickerServiceGateways.getValue(exchangeName).getTickers(
            apiKey = apiKey,
            currencyPairs = currencyPairs,
        )
    }
}
