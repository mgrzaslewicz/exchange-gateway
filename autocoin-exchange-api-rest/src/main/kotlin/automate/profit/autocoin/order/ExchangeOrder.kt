package automate.profit.autocoin.order

import automate.profit.autocoin.exchange.order.ExchangeOrder
import automate.profit.autocoin.exchange.order.ExchangeOrderStatus

fun ExchangeOrder.toOrderDto(exchangeName: String, exchangeId: String, exchangeUserId: String): OrderResponseDto {
    return OrderResponseDto(
        exchangeName = exchangeName,
        exchangeId = exchangeId,
        exchangeUserId = exchangeUserId,
        orderId = orderId,
        baseCurrencyCode = currencyPair.base,
        counterCurrencyCode = currencyPair.counter,
        orderType = type.name,
        orderStatus = if (status == ExchangeOrderStatus.NOT_AVAILABLE) "N/A" else status.name,
        orderedAmount = orderedAmount.toDouble(),
        filledAmount = filledAmount?.toDouble(),
        price = price.toDouble(),
        receivedAtMillis = receivedAtMillis,
        exchangeTimestampMillis = this.exchangeTimestampMillis,
    )
}
