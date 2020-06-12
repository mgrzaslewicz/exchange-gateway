package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.orderbook.OrderBook

interface ExchangeOrderBookService {
    fun getOrderBook(exchangeName: String, currencyPair: CurrencyPair): OrderBook
}

interface UserExchangeOrderBookService {
    fun getOrderBook(currencyPair: CurrencyPair): OrderBook
}