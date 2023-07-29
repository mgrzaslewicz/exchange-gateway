package com.autocoin.exchangegateway.spi.exchange.orderbook.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook
import com.autocoin.exchangegateway.spi.exchange.orderbook.service.authorized.AuthorizedOrderBookServiceFactory

class AuthorizingOrderBookServiceGateway<T>(
    private val authorizedOrderBookServiceFactory: AuthorizedOrderBookServiceFactory<T>,
) : OrderBookServiceGateway<T> {
    override fun getOrderBook(
        exchange: Exchange,
        currencyPair: CurrencyPair,
        apiKey: ApiKeySupplier<T>,
    ): OrderBook {
        return authorizedOrderBookServiceFactory
            .createAuthorizedOrderBookService(
                exchange = exchange,
                apiKey = apiKey,
            )
            .getOrderBook(currencyPair)
    }
}
