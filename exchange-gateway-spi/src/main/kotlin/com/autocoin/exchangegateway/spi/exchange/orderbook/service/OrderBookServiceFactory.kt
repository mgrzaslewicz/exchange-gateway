package com.autocoin.exchangegateway.spi.exchange.orderbook.service

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier

interface OrderBookServiceFactory<T> {

    fun createOrderBookService(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): OrderBookService<T>

}
