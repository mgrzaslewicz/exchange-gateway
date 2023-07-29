package com.autocoin.exchangegateway.spi.exchange.orderbook.service.authorized

import com.autocoin.exchangegateway.spi.exchange.AuthorizedService
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook

interface AuthorizedOrderBookService<T> : AuthorizedService<T> {
    fun getOrderBook(currencyPair: CurrencyPair): OrderBook
}

