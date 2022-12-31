package com.autocoin.exchangegateway.spi.exchange.orderbook.service.authorized

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedOrderBookServiceFactory<T> {
    fun createAuthorizedOrderBookService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedOrderBookService<T>
}
