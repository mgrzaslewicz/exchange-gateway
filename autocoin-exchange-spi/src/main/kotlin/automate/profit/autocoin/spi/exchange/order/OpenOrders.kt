package automate.profit.autocoin.spi.exchange.order

import automate.profit.autocoin.spi.exchange.ExchangeName

interface OpenOrders {
    val exchangeName: ExchangeName
    val exchangeUserId: String
    val openOrders: List<Order>
    val errorMessage: String?
}
