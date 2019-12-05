package automate.profit.autocoin.order

import automate.profit.autocoin.exchange.order.ExchangeOrderType
import automate.profit.autocoin.exchange.order.OrderBookExchangeOrder

data class OrderBookExchangeOrderDto(
        val exchangeName: String,
        val type: ExchangeOrderType,
        val orderedAmount: Double,
        val price: String,
        val baseCurrency: String,
        val counterCurrency: String,
        val timestamp: Long?
)

fun OrderBookExchangeOrder.toOrderBookExchangeOrderDto() = OrderBookExchangeOrderDto(
        exchangeName = exchangeName,
        type = type,
        orderedAmount = orderedAmount.toDouble(),
        price = price.toPlainString(),
        baseCurrency = currencyPair.base,
        counterCurrency = currencyPair.counter,
        timestamp = timestamp?.toEpochMilli()
)