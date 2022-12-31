package com.autocoin.exchangegateway.spi.exchange.ticker.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker
import com.autocoin.exchangegateway.spi.exchange.ticker.service.TickerService

class DelegateTickerServiceGateway<T>(
    private val tickerServiceGateways: Map<ExchangeName, TickerService<T>>,
) : TickerServiceGateway<T> {

    override fun getTicker(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): Ticker {
        return tickerServiceGateways.getValue(exchangeName).getTicker(
            apiKey = apiKey,
            currencyPair = currencyPair,
        )
    }

    override fun getTickers(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPairs: Collection<CurrencyPair>,
    ): List<Ticker> {
        return tickerServiceGateways.getValue(exchangeName).getTickers(
            apiKey = apiKey,
            currencyPairs = currencyPairs,
        )
    }
}
