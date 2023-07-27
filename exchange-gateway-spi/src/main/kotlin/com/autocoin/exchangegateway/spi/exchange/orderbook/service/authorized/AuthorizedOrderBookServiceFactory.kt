package com.autocoin.exchangegateway.spi.exchange.orderbook.service.authorized

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedOrderBookServiceFactory<T> {
    fun createAuthorizedOrderBookService(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedOrderBookService<T>
}
