package automate.profit.autocoin.exchange.order

import automate.profit.autocoin.exchange.currency.CurrencyPair

interface ExchangeOrderBookService {
    fun getOrderBook(exchangeName: String, currencyPair: CurrencyPair): OrderBook
}

interface UserExchangeOrderBookService {
    fun getOrderBook(currencyPair: CurrencyPair): OrderBook
}