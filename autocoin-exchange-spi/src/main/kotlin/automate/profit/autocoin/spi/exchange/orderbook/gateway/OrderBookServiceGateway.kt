package automate.profit.autocoin.spi.exchange.orderbook.gateway

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.orderbook.OrderBook

interface OrderBookServiceGateway {
    fun getOrderBook(exchangeName: ExchangeName, currencyPair: CurrencyPair): OrderBook
}

