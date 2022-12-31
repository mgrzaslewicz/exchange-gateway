package com.autocoin.exchangegateway.spi.exchange.order.service

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface OrderServiceFactory<T> {

    fun createOrderService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): OrderService<T>

}
