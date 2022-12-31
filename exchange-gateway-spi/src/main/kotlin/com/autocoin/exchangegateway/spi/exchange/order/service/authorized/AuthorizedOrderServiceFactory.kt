package com.autocoin.exchangegateway.spi.exchange.order.service.authorized

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedOrderServiceFactory<T> {

    fun createAuthorizedOrderService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedOrderService<T>

}
