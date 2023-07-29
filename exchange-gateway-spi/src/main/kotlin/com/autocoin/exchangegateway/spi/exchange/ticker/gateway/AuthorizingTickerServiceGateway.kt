package com.autocoin.exchangegateway.spi.exchange.ticker.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.ticker.Ticker
import com.autocoin.exchangegateway.spi.exchange.ticker.service.authorized.AuthorizedTickerServiceFactory

class AuthorizingTickerServiceGateway<T>(
    private val authorizedTickerServiceFactory: AuthorizedTickerServiceFactory<T>,
) : TickerServiceGateway<T> {

    override fun getTicker(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): Ticker {
        return authorizedTickerServiceFactory.createAuthorizedTickerService(
            exchange = exchange,
            apiKey = apiKey,
        ).getTicker(currencyPair = currencyPair)
    }

    override fun getTickers(
        exchangeName: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyPairs: Collection<CurrencyPair>,
    ): List<Ticker> {
        return authorizedTickerServiceFactory.createAuthorizedTickerService(
            exchange = exchangeName,
            apiKey = apiKey,
        ).getTickers(currencyPairs = currencyPairs)
    }

}
