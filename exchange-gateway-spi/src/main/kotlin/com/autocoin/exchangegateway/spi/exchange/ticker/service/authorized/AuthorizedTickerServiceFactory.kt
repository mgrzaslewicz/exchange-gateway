package com.autocoin.exchangegateway.spi.exchange.ticker.service.authorized

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedTickerServiceFactory<T> {
    fun createAuthorizedTickerService(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedTickerService<T>
}
