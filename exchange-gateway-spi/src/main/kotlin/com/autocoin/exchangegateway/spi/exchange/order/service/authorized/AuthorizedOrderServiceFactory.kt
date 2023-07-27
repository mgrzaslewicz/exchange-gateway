package com.autocoin.exchangegateway.spi.exchange.order.service.authorized

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface AuthorizedOrderServiceFactory<T> {

    fun createAuthorizedOrderService(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedOrderService<T>

}
