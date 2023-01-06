package com.autocoin.exchangegateway.spi.exchange.orderbook.service

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface OrderBookServiceFactory<T> {

    fun createOrderBookService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): OrderBookService<T>

}
