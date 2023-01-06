package com.autocoin.exchangegateway.spi.exchange.orderbook.service

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook

interface OrderBookService<T> {
    val exchangeName: ExchangeName
    fun getOrderBook(
        apiKey: ApiKeySupplier<T>,
        currencyPair: CurrencyPair,
    ): OrderBook
}


