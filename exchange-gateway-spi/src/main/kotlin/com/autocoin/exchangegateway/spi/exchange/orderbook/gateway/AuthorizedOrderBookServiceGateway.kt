package com.autocoin.exchangegateway.spi.exchange.orderbook.gateway

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook

interface AuthorizedOrderBookServiceGateway {
    fun getOrderBook(
        exchange: Exchange,
        currencyPair: CurrencyPair,
    ): OrderBook
}

