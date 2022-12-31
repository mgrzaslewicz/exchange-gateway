package com.autocoin.exchangegateway.spi.exchange.order

import com.autocoin.exchangegateway.spi.exchange.ExchangeName

interface OpenOrders {
    val exchangeName: ExchangeName
    val exchangeUserId: String
    val openOrders: List<Order>
    val errorMessage: String?
}
