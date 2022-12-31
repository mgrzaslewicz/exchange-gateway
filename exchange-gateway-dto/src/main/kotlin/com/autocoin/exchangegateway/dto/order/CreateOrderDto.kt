package com.autocoin.exchangegateway.dto.order

import com.autocoin.exchangegateway.dto.SerializableToJson
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide

data class CreateOrderDto(
    val exchangeName: String,
    val exchangeUserId: String,
    val orderSide: OrderSide,
    val baseCurrencyCode: String,
    val counterCurrencyCode: String,
    val price: String,
    val amount: String,
) : SerializableToJson {

    override fun appendJson(builder: StringBuilder) = builder
        .append("{")
        .append("\"exchangeName\":\"$exchangeName\",")
        .append("\"exchangeUserId\":\"$exchangeUserId\",")
        .append("\"orderSide\":\"$orderSide\",")
        .append("\"baseCurrencyCode\":\"$baseCurrencyCode\",")
        .append("\"counterCurrencyCode\":\"$counterCurrencyCode\",")
        .append("\"price\":\"$price\",")
        .append("\"amount\":\"$amount\"")
        .append("}")

}
