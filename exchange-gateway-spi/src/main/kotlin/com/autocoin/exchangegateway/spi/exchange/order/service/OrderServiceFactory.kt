package com.autocoin.exchangegateway.spi.exchange.order.service

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface OrderServiceFactory<T> {

    fun createOrderService(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): OrderService<T>

}
