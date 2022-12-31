package com.autocoin.exchangegateway.api.exchange.order

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.order.Order
import com.autocoin.exchangegateway.spi.exchange.order.OpenOrders as SpiOpenOrders

data class OpenOrders(
    override val exchangeName: ExchangeName,
    override val exchangeUserId: String,
    override val openOrders: List<Order>,
    override val errorMessage: String?,
) : SpiOpenOrders
