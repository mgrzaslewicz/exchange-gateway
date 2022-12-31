package com.autocoin.exchangegateway.spi.exchange.ticker.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker
import com.autocoin.exchangegateway.spi.exchange.ticker.service.authorized.AuthorizedTickerServiceFactory

class TickerServiceGatewayUsingAuthorizedTickerService<T>(
    private val authorizedTickerServiceFactory: AuthorizedTickerServiceFactory<T>,
) : TickerServiceGateway<T> {

    override fun getTicker(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): Ticker {
        return authorizedTickerServiceFactory.createAuthorizedTickerService(
            exchangeName = exchangeName,
            apiKey = apiKey,
        ).getTicker(currencyPair = currencyPair)
    }

    override fun getTickers(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyPairs: Collection<CurrencyPair>,
    ): List<Ticker> {
        return authorizedTickerServiceFactory.createAuthorizedTickerService(
            exchangeName = exchangeName,
            apiKey = apiKey,
        ).getTickers(currencyPairs = currencyPairs)
    }

}
