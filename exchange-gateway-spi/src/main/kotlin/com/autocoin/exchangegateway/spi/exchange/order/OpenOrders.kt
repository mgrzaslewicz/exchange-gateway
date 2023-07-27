package com.autocoin.exchangegateway.spi.exchange.order


interface OpenOrders {
    val exchangeUserId: String
    val openOrders: List<Order>
    val errorMessage: String?
}
