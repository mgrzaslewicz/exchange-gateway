package com.autocoin.exchangegateway.spi.exchange.orderbook.service.authorized

import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.orderbook.OrderBook

interface AuthorizedOrderBookService<T> : com.autocoin.exchangegateway.spi.exchange.AuthorizedService<T> {
    fun getOrderBook(currencyPair: CurrencyPair): OrderBook
}

