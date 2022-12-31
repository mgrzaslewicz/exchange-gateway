package automate.profit.autocoin.order

import automate.profit.autocoin.exchange.order.ExchangeOrderType

data class CreateOrderDto(
        val exchangeName: String,
        val exchangeUserId: String,
        val orderType: ExchangeOrderType,
        val baseCurrencyCode: String,
        val counterCurrencyCode: String,
        val price: Double,
        val amount: Double
)
