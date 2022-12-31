package automate.profit.autocoin.api.exchange.order

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.order.Order
import automate.profit.autocoin.spi.exchange.order.OpenOrders as SpiOpenOrders

data class OpenOrders(
    override val exchangeName: ExchangeName,
    override val exchangeUserId: String,
    override val openOrders: List<Order>,
    override val errorMessage: String?,
) : SpiOpenOrders
