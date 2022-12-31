package com.autocoin.exchangegateway.spi.exchange.orderbook.service

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey
import java.util.function.Supplier

interface OrderBookServiceFactory {

    fun createOrderBookService(
        exchangeName: ExchangeName,
        apiKey: Supplier<ApiKey>?,
    ): OrderBookService

}
