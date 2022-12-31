package com.autocoin.exchangegateway.dto.order

import com.autocoin.exchangegateway.api.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.api.exchange.order.Order
import com.autocoin.exchangegateway.dto.SerializableToJson
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import com.autocoin.exchangegateway.spi.exchange.order.OrderStatus
import com.autocoin.exchangegateway.spi.exchange.order.Order as SpiOrder

data class OrderDto(
    val exchangeName: String,
    val exchangeOrderId: String,
    val side: String,
    val baseCurrencyCode: String,
    val counterCurrencyCode: String,
    val status: String,
    val orderedAmount: String,
    val filledAmount: String?,
    val price: String,
    val receivedAtMillis: Long,
    val exchangeTimestampMillis: Long?,
) : SerializableToJson {
    fun toOrder(): SpiOrder {
        return Order(
            exchangeName = ExchangeName(exchangeName),
            exchangeOrderId = exchangeOrderId,
            side = OrderSide.valueOf(side),
            orderedAmount = orderedAmount.toBigDecimal(),
            filledAmount = filledAmount?.toBigDecimal(),
            price = price.toBigDecimal(),
            currencyPair = CurrencyPair.of(baseCurrencyCode, counterCurrencyCode),
            status = OrderStatus.valueOf(status),
            receivedAtMillis = receivedAtMillis,
            exchangeTimestampMillis = exchangeTimestampMillis,
        )
    }

    override fun appendJson(builder: StringBuilder) = builder
        .append("{")
        .append("\"exchangeName\":\"$exchangeName\",")
        .append("\"exchangeOrderId\":\"$exchangeOrderId\",")
        .append("\"side\":\"$side\",")
        .append("\"baseCurrencyCode\":\"$baseCurrencyCode\",")
        .append("\"counterCurrencyCode\":\"$counterCurrencyCode\",")
        .append("\"status\":\"$status\",")
        .append("\"orderedAmount\":\"$orderedAmount\",")
        .append("\"filledAmount\":\"$filledAmount\",")
        .append("\"price\":\"$price\",")
        .append("\"receivedAtMillis\":$receivedAtMillis,")
        .append("\"exchangeTimestampMillis\":$exchangeTimestampMillis")
        .append("}")
}

fun SpiOrder.toDto(): OrderDto {
    return OrderDto(
        exchangeName = exchangeName.value,
        exchangeOrderId = exchangeOrderId,
        baseCurrencyCode = currencyPair.base,
        counterCurrencyCode = currencyPair.counter,
        side = side.name,
        status = status.name,
        orderedAmount = orderedAmount.toPlainString(),
        filledAmount = filledAmount?.toPlainString(),
        price = price.toPlainString(),
        receivedAtMillis = receivedAtMillis,
        exchangeTimestampMillis = this.exchangeTimestampMillis,
    )
}

