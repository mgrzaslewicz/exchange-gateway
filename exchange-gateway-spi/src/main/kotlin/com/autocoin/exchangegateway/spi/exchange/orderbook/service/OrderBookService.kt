package com.autocoin.exchangegateway.spi.exchange.orderbook.service

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKey
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook
import java.util.function.Supplier

interface OrderBookService {
    val exchangeName: ExchangeName
    fun getOrderBook(
        apiKey: Supplier<ApiKey>?,
        currencyPair: CurrencyPair,
    ): OrderBook
}


