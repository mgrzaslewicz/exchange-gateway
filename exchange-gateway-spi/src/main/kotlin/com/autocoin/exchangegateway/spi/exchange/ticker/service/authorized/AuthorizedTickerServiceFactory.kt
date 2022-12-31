package com.autocoin.exchangegateway.spi.exchange.ticker.service.authorized

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedTickerServiceFactory<T> {
    fun createAuthorizedTickerService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedTickerService<T>
}
