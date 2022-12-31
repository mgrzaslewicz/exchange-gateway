package automate.profit.autocoin.spi.exchange.orderbook.service.authorized

import automate.profit.autocoin.spi.exchange.AuthorizedService
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.orderbook.OrderBook

interface AuthorizedOrderBookService<T> : AuthorizedService<T> {
    fun getOrderBook(currencyPair: CurrencyPair): OrderBook
}

