package com.autocoin.exchangegateway.spi.exchange.orderbook.gateway

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook

interface OrderBookServiceGateway {
    fun getOrderBook(
        exchangeName: ExchangeName,
        currencyPair: CurrencyPair,
    ): OrderBook
}

