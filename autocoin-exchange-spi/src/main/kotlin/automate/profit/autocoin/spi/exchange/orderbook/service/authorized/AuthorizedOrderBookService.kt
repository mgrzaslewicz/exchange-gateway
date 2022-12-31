package automate.profit.autocoin.spi.exchange.orderbook.service.authorized

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.orderbook.OrderBook

interface AuthorizedOrderBookService {
    val exchangeName: ExchangeName
    fun getOrderBook(currencyPair: CurrencyPair): OrderBook
}

